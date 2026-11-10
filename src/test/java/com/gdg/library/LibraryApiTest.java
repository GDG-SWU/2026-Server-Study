package com.gdg.library;

import com.fasterxml.jackson.databind.*;
import com.gdg.library.book.*;
import com.gdg.library.common.BusinessException;
import com.gdg.library.loan.*;
import com.gdg.library.user.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.*;
import java.util.List;
import java.time.*;
import java.util.concurrent.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LibraryApiTest.FixedClockConfig.class)
class LibraryApiTest extends ApiTestSupport {
    @Autowired MockMvc mvc;
    @Autowired org.springframework.web.context.WebApplicationContext context;
    @Autowired com.gdg.library.security.JwtTokenProvider tokens;
    @Autowired ObjectMapper mapper;
    @Autowired LoanRepository loans;
    @Autowired BookRepository books;
    @Autowired UserRepository users;
    @Autowired LoanService loanService;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired JdbcTemplate jdbc;

    @TestConfiguration
    static class FixedClockConfig {
        @Bean
        @Primary
        Clock testClock() {
            return Clock.fixed(Instant.parse("2026-01-30T15:30:00Z"), ZoneId.of("Asia/Seoul"));
        }
    }

    private Long userId;
    private Long bookId;

    @BeforeEach
    void setUp() throws Exception {
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity()).build();
        loans.deleteAll();
        books.deleteAll();
        users.deleteAll();
        userId = create("/users/signup", userBody("sua@example.com", "practice-password")).get("id").asLong();
        jdbc.update("update \"user\" set role = 'ADMIN' where id = ?", userId);
        String token = tokens.issue(users.findById(userId).orElseThrow());
        mvc = org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup(context)
                .apply(org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity())
                .defaultRequest(get("/").header("Authorization", "Bearer " + token)).build();
        bookId = create("/books", "{\"title\":\"자바의 정석\",\"author\":\"남궁성\"}").get("id").asLong();
    }

    @Test
    void loanLifecycleReturnsDtosAndUpdatesBook() throws Exception {
        JsonNode loan = create("/loans", loanBody());
        long id = loan.get("id").asLong();
        assertThat(loan.size()).isEqualTo(6);
        assertThat(loan.get("userId").asLong()).isEqualTo(userId);
        assertThat(loan.get("bookId").asLong()).isEqualTo(bookId);
        assertThat(loan.get("returnDate").isNull()).isTrue();
        assertThat(loan.get("loanDate").asText()).isEqualTo("2026-01-31");
        assertThat(loan.get("dueDate").asText()).isEqualTo("2026-02-14");
        Loan stored = loans.findById(id).orElseThrow();
        assertThat(stored.getDueDate()).isEqualTo(stored.getLoanDate().plusDays(14));
        mvc.perform(get("/books/{id}", bookId)).andExpect(jsonPath("$.data.isBorrowed").value(true));
        mvc.perform(get("/loans").param("userId", userId.toString())).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(id));
        mvc.perform(get("/loans/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(id));
        mvc.perform(put("/loans/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.returnDate").value("2026-01-31"))
                .andExpect(jsonPath("$.data.dueDate").value("2026-02-14"));
        mvc.perform(get("/books/{id}", bookId)).andExpect(jsonPath("$.data.isBorrowed").value(false));
        mvc.perform(delete("/loans/{id}", id)).andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true)).andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()));
        mvc.perform(get("/loans/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void duplicateLoanAndRepeatedReturnAreConflicts() throws Exception {
        long id = create("/loans", loanBody()).get("id").asLong();
        mvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("BOOK_ALREADY_BORROWED"));
        assertThat(loans.count()).isEqualTo(1);
        mvc.perform(put("/loans/{id}", id)).andExpect(status().isOk());
        mvc.perform(put("/loans/{id}", id)).andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("LOAN_ALREADY_RETURNED"));
        create("/loans", loanBody());
    }

    @Test
    void deletingActiveLoanMakesBookAvailable() throws Exception {
        long id = create("/loans", loanBody()).get("id").asLong();
        mvc.perform(delete("/loans/{id}", id)).andExpect(status().isOk());
        mvc.perform(get("/books/{id}", bookId)).andExpect(jsonPath("$.data.isBorrowed").value(false));
        create("/loans", loanBody());
    }

    @Test
    void deletingOldReturnedLoanDoesNotReleaseNewLoan() throws Exception {
        long oldId = create("/loans", loanBody()).get("id").asLong();
        mvc.perform(put("/loans/{id}", oldId)).andExpect(status().isOk());
        create("/loans", loanBody());
        mvc.perform(delete("/loans/{id}", oldId)).andExpect(status().isOk());
        mvc.perform(get("/books/{id}", bookId)).andExpect(jsonPath("$.data.isBorrowed").value(true));
        mvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(loanBody()))
                .andExpect(status().isConflict());
    }

    @Test
    void missingUserOrBookDoesNotChangeState() throws Exception {
        assertThatThrownBy(() -> loanService.create(new LoanCreateRequest(bookId), 999999L))
                .isInstanceOf(BusinessException.class);
        mvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\":" + userId + ",\"bookId\":999999}"))
                .andExpect(status().isNotFound());
        assertThat(loans.count()).isZero();
        mvc.perform(get("/books/{id}", bookId)).andExpect(jsonPath("$.data.isBorrowed").value(false));
    }

    @Test
    void validatesBodiesAndPathVariables() throws Exception {
        for (String body : List.of("{}", "{\"userId\":0,\"bookId\":-1}", "{broken")) {
            mvc.perform(post("/loans").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(userBody("not-an-email", " ")))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"\",\"author\":\"\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/loans/not-a-number")).andExpect(status().isBadRequest());
    }

    @Test
    void usersAndBooksSupportListReadAndDelete() throws Exception {
        mvc.perform(get("/users")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].email").value("sua@example.com"))
                .andExpect(jsonPath("$.data[0].password").doesNotExist());
        mvc.perform(get("/users/{id}", userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createdAt").value("2026-01-31T00:30:00"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
        mvc.perform(get("/books")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].isBorrowed").value(false));
        mvc.perform(delete("/books/{id}", bookId)).andExpect(status().isOk());
        mvc.perform(delete("/users/{id}", userId)).andExpect(status().isOk());
        mvc.perform(get("/books/{id}", bookId)).andExpect(status().isNotFound());
        mvc.perform(get("/users/{id}", userId)).andExpect(status().isNotFound());
    }

    @Test
    void referencedUserAndBookCannotBeDeleted() throws Exception {
        create("/loans", loanBody());
        mvc.perform(delete("/users/{id}", userId)).andExpect(status().isConflict());
        mvc.perform(delete("/books/{id}", bookId)).andExpect(status().isConflict());
    }

    @Test
    void missingLoanEndpointsReturn404() throws Exception {
        mvc.perform(get("/loans/999999")).andExpect(status().isNotFound());
        mvc.perform(put("/loans/999999")).andExpect(status().isNotFound());
        mvc.perform(delete("/loans/999999")).andExpect(status().isNotFound());
    }

    @Test
    void swaggerDocumentsAllResourcesAndReturnOperation() throws Exception {
        mvc.perform(get("/v3/api-docs")).andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/loans'].post").exists())
                .andExpect(jsonPath("$.paths['/loans/{id}'].put.summary").value("도서 반납"))
                .andExpect(jsonPath("$.paths['/users'].post.responses['201']").exists())
                .andExpect(jsonPath("$.paths['/books'].post.responses['201']").exists())
                .andExpect(jsonPath("$.components.schemas.LoanResponse.properties.dueDate").exists())
                .andExpect(jsonPath("$.components.schemas.UserResponse.properties.password").doesNotExist())
                .andExpect(jsonPath("$.components.schemas.UserCreateRequest.properties.email").exists());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
    }

    @Test
    void simultaneousLoanRequestsAllowOnlyOneBorrower() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        Callable<Integer> attempt = () -> {
            ready.countDown();
            if (!start.await(10, TimeUnit.SECONDS)) throw new IllegalStateException("start timeout");
            try {
                loanService.create(new LoanCreateRequest(bookId), userId);
                return 201;
            } catch (BusinessException exception) {
                return exception.getErrorCode().getStatus().value();
            }
        };
        try {
            Future<Integer> first = executor.submit(attempt);
            Future<Integer> second = executor.submit(attempt);
            assertThat(ready.await(10, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            assertThat(List.of(first.get(15, TimeUnit.SECONDS), second.get(15, TimeUnit.SECONDS)))
                    .containsExactlyInAnyOrder(201, 409);
            assertThat(loans.count()).isEqualTo(1);
            assertThat(books.findById(bookId).orElseThrow().isBorrowed()).isTrue();
        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void registrationStoresHashedPasswordAndReturnsOnlyPublicFields() throws Exception {
        String rawPassword = "가".repeat(24);
        JsonNode response = create("/users", userBody("second@example.com", rawPassword));
        assertThat(response.size()).isEqualTo(3);
        assertThat(response.has("password")).isFalse();
        assertThat(response.has("name")).isFalse();
        User stored = users.findById(response.get("id").asLong()).orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo(rawPassword);
        assertThat(stored.getPassword().length()).isLessThanOrEqualTo(255);
        assertThat(passwordEncoder.matches(rawPassword, stored.getPassword())).isTrue();
        assertThat(passwordEncoder.matches("wrong-password", stored.getPassword())).isFalse();
        assertThat(stored.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 31, 0, 30));
    }

    @Test
    void duplicateEmailsAreRejectedByApiAndDatabase() throws Exception {
        mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(userBody("sua@example.com", "different-password")))
                .andExpect(status().isConflict());
        assertThat(users.count()).isEqualTo(1);
        assertThatThrownBy(() -> jdbc.update(
                "insert into \"user\" (email, password) values (?, ?)", "sua@example.com", "test-hash"))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    void validatesRequiredUserFieldsAndErdLengthLimits() throws Exception {
        for (String body : List.of("{}", userBody("", "valid-password"),
                userBody("invalid", "valid-password"), userBody("valid@example.com", ""),
                userBody("a".repeat(64) + "@" + "b".repeat(30) + ".comxx", "valid-password"),
                userBody("valid@example.com", "a".repeat(256)))) {
            mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        String title = "제".repeat(255);
        String author = "저".repeat(255);
        JsonNode book = create("/books", mapper.writeValueAsString(new BookCreateRequest(title, author)));
        assertThat(book.get("title").asText()).isEqualTo(title);
        assertThat(book.get("author").asText()).isEqualTo(author);
        for (BookCreateRequest request : List.of(new BookCreateRequest(title + "제", author),
                new BookCreateRequest(title, author + "저"))) {
            mvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(request))).andExpect(status().isBadRequest());
        }
    }

    @Test
    void databaseSchemaMatchesErdDefaultsAndRequiredDueDate() {
        jdbc.update("insert into book (title, author) values ('DB 도서', 'DB 저자')");
        Long id = jdbc.queryForObject("select id from book where title = 'DB 도서'", Long.class);
        assertThat(books.findById(id).orElseThrow().isBorrowed()).isFalse();
        assertThatThrownBy(() -> jdbc.update("insert into loan (user_id, book_id, loan_date) values (?, ?, ?)",
                userId, bookId, LocalDate.of(2026, 1, 31)))
                .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    private String userBody(String email, String password) throws Exception {
        return mapper.writeValueAsString(new UserCreateRequest(email, password));
    }

    @Test
    void errorsUseCommonEnvelopeAndExpectedCodes() throws Exception {
        assertError(get("/books/999999"), 404, "BOOK_NOT_FOUND");
        assertError(get("/users/999999"), 404, "USER_NOT_FOUND");
        assertError(get("/loans/999999"), 404, "LOAN_NOT_FOUND");
        assertError(get("/loans").param("userId", "999999"), 404, "USER_NOT_FOUND");
        assertError(get("/loans"), 400, "INVALID_REQUEST");
        assertError(get("/loans").param("userId", "abc"), 400, "INVALID_REQUEST");
        assertError(post("/books").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"\",\"author\":\"\"}"), 400, "INVALID_REQUEST");
        assertError(post("/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{}"), 400, "INVALID_REQUEST");
        assertError(post("/loans").contentType(MediaType.APPLICATION_JSON)
                .content("{broken"), 400, "INVALID_REQUEST");
        assertError(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(userBody("sua@example.com", "another-password")), 409, "DUPLICATE_EMAIL");
        assertError(get("/missing-url"), 404, "NOT_FOUND");
        assertError(put("/books"), 405, "METHOD_NOT_ALLOWED");
    }

    @Test
    void memberLoanListFiltersOtherMembersAndAllowsEmptyHistory() throws Exception {
        long secondUserId = create("/users", userBody("other@example.com", "password")).get("id").asLong();
        mvc.perform(get("/loans").param("userId", userId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
        long loanId = create("/loans", loanBody()).get("id").asLong();
        mvc.perform(get("/loans").param("userId", userId.toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(loanId));
        mvc.perform(get("/loans").param("userId", Long.toString(secondUserId)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void unexpectedErrorsHideInternalDetails() throws Exception {
        BookService service = org.mockito.Mockito.mock(BookService.class);
        org.mockito.Mockito.when(service.findById(1L))
                .thenThrow(new RuntimeException("secret SQL and internal class details"));
        MockMvc isolatedMvc = org.springframework.test.web.servlet.setup.MockMvcBuilders
                .standaloneSetup(new BookController(service))
                .setControllerAdvice(new com.gdg.library.common.GlobalExceptionHandler())
                .build();
        isolatedMvc.perform(get("/books/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("서버 오류가 발생했습니다."))
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("secret SQL"))));
    }

    private void assertError(RequestBuilder request, int httpStatus, String code) throws Exception {
        MvcResult result = mvc.perform(request).andExpect(status().is(httpStatus))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue()))
                .andReturn();
        assertThat(mapper.readTree(result.getResponse().getContentAsByteArray()).size()).isEqualTo(4);
    }

    private String loanBody() {
        return "{\"bookId\":" + bookId + "}";
    }

    private JsonNode create(String path, String body) throws Exception {
        MvcResult result = mvc.perform(post(path).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated()).andReturn();
        JsonNode response = mapper.readTree(result.getResponse().getContentAsByteArray());
        assertThat(response.size()).isEqualTo(4);
        assertThat(response.get("success").asBoolean()).isTrue();
        assertThat(response.get("code").asText()).isEqualTo("SUCCESS");
        assertThat(response.get("message").asText()).isNotBlank();
        return response.get("data");
    }
}
