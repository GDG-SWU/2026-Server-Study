package com.gdg.library;

import com.fasterxml.jackson.databind.*;
import com.gdg.library.book.BookRepository;
import com.gdg.library.loan.LoanRepository;
import com.gdg.library.user.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.util.Date;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(LibraryApiTest.FixedClockConfig.class)
class JwtSecurityTest extends ApiTestSupport {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository users;
    @Autowired LoanRepository loans;
    @Autowired BookRepository books;
    @Autowired PasswordEncoder passwords;
    @Autowired JdbcTemplate jdbc;
    @Autowired Clock clock;
    private static final String PASSWORD = "password123";

    @BeforeEach
    void cleanDatabase() {
        loans.deleteAll();
        books.deleteAll();
        users.deleteAll();
    }

    @Test
    void signupAlwaysCreatesUserWithBcryptAndRejectsDuplicate() throws Exception {
        JsonNode created = data(mvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"a@test.com\",\"password\":\"password123\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isCreated()).andReturn());
        User user = users.findById(created.get("id").asLong()).orElseThrow();
        assertThat(user.getRole()).isEqualTo(Role.USER);
        assertThat(user.getPassword()).startsWith("$2a$10$");
        assertThat(passwords.matches(PASSWORD, user.getPassword())).isTrue();
        assertThat(created.has("password")).isFalse();
        error(post("/users/signup").contentType(MediaType.APPLICATION_JSON).content(credentials("a@test.com", PASSWORD)),
                409, "DUPLICATE_EMAIL");
        String token = login("a@test.com");
        error(post("/books").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                .content(bookBody()), 403, "FORBIDDEN");
    }

    @Test
    void rejectsPasswordsOverBcryptByteLimit() throws Exception {
        error(post("/users/signup").contentType(MediaType.APPLICATION_JSON)
                .content(credentials("a@test.com", "가".repeat(25))), 400, "INVALID_REQUEST");
        assertThat(users.count()).isZero();
    }

    @Test
    void loginRejectsWrongPasswordAndUnknownEmail() throws Exception {
        signup("a@test.com");
        error(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(credentials("a@test.com", "wrong")), 401, "LOGIN_FAILED");
        error(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(credentials("missing@test.com", PASSWORD)), 401, "LOGIN_FAILED");
        error(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                .content("{}"), 400, "INVALID_REQUEST");
        assertThat(login("a@test.com")).isNotBlank();
    }

    @Test
    void missingTokenReturnsCommon401WithoutSession() throws Exception {
        MvcResult result = error(get("/books"), 401, "UNAUTHORIZED");
        assertThat(result.getRequest().getSession(false)).isNull();
        error(get("/users/me"), 401, "UNAUTHORIZED");
        error(post("/users").contentType(MediaType.APPLICATION_JSON)
                .content(credentials("a@test.com", PASSWORD)), 401, "UNAUTHORIZED");
        error(get("/h2-console/"), 401, "UNAUTHORIZED");
    }

    @Test
    void userCanReadButCannotCreateOrDeleteBooks() throws Exception {
        signup("a@test.com");
        String token = login("a@test.com");
        mvc.perform(get("/books").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        error(post("/books").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                .content(bookBody()), 403, "FORBIDDEN");
        error(delete("/books/1").header("Authorization", bearer(token)), 403, "FORBIDDEN");
    }

    @Test
    void promotionNeedsNewLoginAndAdminCanCreateAndDelete() throws Exception {
        Long id = signup("a@test.com");
        String oldToken = login("a@test.com");
        jdbc.update("update \"user\" set role = 'ADMIN' where id = ?", id);
        error(post("/books").header("Authorization", bearer(oldToken)).contentType(MediaType.APPLICATION_JSON)
                .content(bookBody()), 403, "FORBIDDEN");
        String newToken = login("a@test.com");
        JsonNode book = createBook(newToken);
        mvc.perform(delete("/books/{id}", book.get("id").asLong()).header("Authorization", bearer(newToken)))
                .andExpect(status().isOk());
        assertThat(books.count()).isZero();
    }

    @Test
    void malformedTamperedAndWrongKeyTokensAreRejected() throws Exception {
        signup("a@test.com");
        String token = login("a@test.com");
        int signatureStart = token.lastIndexOf('.') + 1;
        char replacement = token.charAt(signatureStart) == 'A' ? 'B' : 'A';
        String tampered = token.substring(0, signatureStart) + replacement + token.substring(signatureStart + 1);
        for (String header : new String[] {bearer(tampered), "Bearer broken", "Bearer ", "Basic abc"}) {
            error(get("/books").header("Authorization", header), 401, "INVALID_TOKEN");
        }
        for (char last : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray()) {
            if (last != token.charAt(token.length() - 1)) {
                error(get("/books").header("Authorization", bearer(token.substring(0, token.length() - 1) + last)),
                        401, "INVALID_TOKEN");
            }
        }
        String wrongKey = Jwts.builder().subject("1").claim("role", "ADMIN")
                .expiration(Date.from(clock.instant().plusSeconds(60)))
                .signWith(Jwts.SIG.HS256.key().build()).compact();
        error(get("/books").header("Authorization", bearer(wrongKey)), 401, "INVALID_TOKEN");
    }

    @Test
    void expiredAndIncompleteClaimsAreRejected() throws Exception {
        var key = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
        String expired = Jwts.builder().subject("1").claim("role", "USER")
                .expiration(Date.from(clock.instant().minusSeconds(1))).signWith(key).compact();
        String missingExpiry = Jwts.builder().subject("1").claim("role", "USER").signWith(key).compact();
        String missingRole = Jwts.builder().subject("1")
                .expiration(Date.from(clock.instant().plusSeconds(60))).signWith(key).compact();
        String unknownRole = Jwts.builder().subject("1").claim("role", "ROOT")
                .expiration(Date.from(clock.instant().plusSeconds(60))).signWith(key).compact();
        for (String token : new String[] {expired, missingExpiry, missingRole, unknownRole}) {
            error(get("/books").header("Authorization", bearer(token)), 401, "INVALID_TOKEN");
        }
    }

    @Test
    void loanUsesTokenIdentityAndOtherMemberCannotReturnIt() throws Exception {
        Long ownerId = signup("owner@test.com");
        Long otherId = signup("other@test.com");
        Long adminId = signup("admin@test.com");
        jdbc.update("update \"user\" set role = 'ADMIN' where id = ?", adminId);
        long bookId = createBook(login("admin@test.com")).get("id").asLong();
        String ownerToken = login("owner@test.com");
        JsonNode loan = data(mvc.perform(post("/loans").header("Authorization", bearer(ownerToken))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":" + bookId + ",\"userId\":" + otherId + "}"))
                .andExpect(status().isCreated()).andReturn());
        assertThat(loan.get("userId").asLong()).isEqualTo(ownerId);
        long loanId = loan.get("id").asLong();
        error(put("/loans/{id}", loanId).header("Authorization", bearer(login("other@test.com"))),
                403, "FORBIDDEN");
        assertThat(loans.findById(loanId).orElseThrow().getReturnDate()).isNull();
        assertThat(books.findById(bookId).orElseThrow().isBorrowed()).isTrue();
        mvc.perform(put("/loans/{id}", loanId).header("Authorization", bearer(ownerToken)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.returnDate").isNotEmpty());
    }

    @Test
    void loanWithoutUserIdAndMeEndpointWork() throws Exception {
        Long id = signup("a@test.com");
        jdbc.update("update \"user\" set role = 'ADMIN' where id = ?", id);
        String token = login("a@test.com");
        long bookId = createBook(token).get("id").asLong();
        mvc.perform(post("/loans").header("Authorization", bearer(token)).contentType(MediaType.APPLICATION_JSON)
                .content("{\"bookId\":" + bookId + "}"))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.data.userId").value(id));
        mvc.perform(get("/users/me").header("Authorization", bearer(token)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.id").value(id))
                .andExpect(jsonPath("$.data.email").value("a@test.com"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void swaggerAndApiDocsStayPublicAndDocumentBearer() throws Exception {
        mvc.perform(get("/swagger-ui.html")).andExpect(status().is3xxRedirection());
        mvc.perform(get("/swagger-ui/index.html")).andExpect(status().isOk());
        JsonNode docs = mapper.readTree(mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsByteArray());
        assertThat(docs.at("/components/securitySchemes/bearerAuth/scheme").asText()).isEqualTo("bearer");
        assertThat(docs.at("/components/schemas/LoanCreateRequest/properties/userId").isMissingNode()).isTrue();
        assertThat(docs.at("/paths/~1users~1signup/post/security").isMissingNode()
                || docs.at("/paths/~1users~1signup/post/security").isEmpty()).isTrue();
    }

    private Long signup(String email) throws Exception {
        return data(mvc.perform(post("/users/signup").contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email, PASSWORD))).andExpect(status().isCreated()).andReturn()).get("id").asLong();
    }

    private String login(String email) throws Exception {
        return data(mvc.perform(post("/users/login").contentType(MediaType.APPLICATION_JSON)
                .content(credentials(email, PASSWORD))).andExpect(status().isOk()).andReturn()).get("accessToken").asText();
    }

    private JsonNode createBook(String token) throws Exception {
        return data(mvc.perform(post("/books").header("Authorization", bearer(token))
                .contentType(MediaType.APPLICATION_JSON).content(bookBody()))
                .andExpect(status().isCreated()).andReturn());
    }

    private String credentials(String email, String password) throws Exception {
        return mapper.writeValueAsString(new LoginRequest(email, password));
    }

    private String bookBody() {
        return "{\"title\":\"자바\",\"author\":\"스터디\"}";
    }

    private String bearer(String token) { return "Bearer " + token; }

    private JsonNode data(MvcResult result) throws Exception {
        return mapper.readTree(result.getResponse().getContentAsByteArray()).get("data");
    }

    private MvcResult error(MockHttpServletRequestBuilder request, int status, String code) throws Exception {
        MvcResult result = mvc.perform(request).andExpect(status().is(status))
                .andExpect(jsonPath("$.success").value(false)).andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.data").value(org.hamcrest.Matchers.nullValue())).andReturn();
        assertThat(mapper.readTree(result.getResponse().getContentAsByteArray()).size()).isEqualTo(4);
        return result;
    }
}
