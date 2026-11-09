# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

A weekly study project: a library (도서 대출) REST API built with Spring Boot 4.1 / Java 17 / Gradle, MySQL via JPA, JWT auth with Spring Security, Swagger via springdoc. Code comments and user-facing messages are in Korean — keep new ones consistent.

Work happens on per-week branches named `week-NN/sebin` (e.g. `week-06/sebin`), branched off and PR'd into `main`. Commit messages use a `feat:` / `fix:` prefix, usually followed by a Korean description.

## Commands

On Windows use `gradlew.bat` (or `./gradlew` from Git Bash).

```bash
./gradlew build                 # compile + test, produces build/libs/*.jar
./gradlew bootRun               # run locally (port 8080)
./gradlew test                  # all tests
./gradlew test --tests "com.example.server_study_2026.ServerStudy2026ApplicationTests"   # single test class
./gradlew test --tests "*ClassName.methodName"                                            # single method
```

There is no linter configured. The only test is a `@SpringBootTest` context-load test, which needs a reachable MySQL and the env vars below.

### Environment / profiles

- Required env vars: `DB_PASSWORD`, `JWT_SECRET` (HMAC key; must be ≥ 32 bytes for HS256). `.env` is gitignored.
- **Default profile** (`application.yml`): connects to `localhost:3306/library` as `root`. `spring-boot-docker-compose` (developmentOnly) auto-starts the MySQL 8.4 container from `compose.yaml` during `bootRun`.
- **`prod` profile** (`application-prod.yml`, used for the AWS deployment): port `5000`, datasource from `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`, docker-compose disabled, SQL logging off. Run with `--spring.profiles.active=prod`.
- `ddl-auto: update` in both profiles — schema is derived from entities; there are no migrations.
- Swagger UI: `/swagger-ui.html`. Health check (unauthenticated): `GET /health`.

## Architecture

Package root `com.example.server_study_2026`, layered by role and then by feature:

- `api/<feature>/` — `@RestController`s plus `dto/` (Java records: `*Request` with Bean Validation, `*Response` with a static `from(entity)` factory). Controllers return `ApiResponse<T>` directly (not `ResponseEntity`), using `@ResponseStatus` for non-200 codes.
- `service/<feature>/` — business logic. Classes are `@Transactional(readOnly = true)`; write methods add `@Transactional`. Services take and return DTOs, mapping entities via `XxxResponse.from(...)`.
- `domain/<feature>/` — JPA entities + Spring Data repositories. Entities use `@NoArgsConstructor(access = PROTECTED)`, a `@Builder` constructor, and no setters; state changes go through domain methods (e.g. `Book.borrow()`, `Loan.completeReturn()` which also calls `book.returnBook()`). Business rules live in entities where possible (e.g. `Loan` sets `dueDate = loanDate + 14 days`). `BaseEntity` provides `createdAt` via JPA auditing (`@EnableJpaAuditing` on the main class).
- `global/` — cross-cutting: `response/ApiResponse`, `exception/`, `security/`, `common/`, `health/`.

### Response & error contract

Every response — success or failure — is the `ApiResponse<T>(success, code, message, data)` envelope.

- To fail, throw `new BusinessException(ErrorCode.X)`. Add new cases to the `ErrorCode` enum (HTTP status + Korean message); the enum `name()` becomes the response `code`.
- `GlobalExceptionHandler` is scoped to `basePackages = "...api"`, so it only covers controllers under `api/` (not `global/health`). It maps `BusinessException`, validation errors (`INVALID_REQUEST` with `field: message` joined), malformed requests, and a catch-all `INTERNAL_ERROR`.
- Security errors happen before the controller layer, so they are written separately by `JwtAuthenticationEntryPoint` (401) / `JwtAccessDeniedHandler` (403) through `ErrorResponseWriter`, using the same envelope.

### Authentication flow

- `POST /users/login` issues a JWT (`JwtProvider.createToken`) with `sub = userId` and a `role` claim (`USER` / `ADMIN`; new signups are always `USER`). Passwords are BCrypt-hashed.
- `JwtAuthenticationFilter` reads `Authorization: Bearer <token>` and sets the authentication **principal to the `Long` userId**, with authority `ROLE_<role>`. Controllers get the current user via `@AuthenticationPrincipal Long userId` — don't accept userId from the request body/params.
- On a bad token the filter does not reject; it stores `ErrorCode.EXPIRED_TOKEN` / `INVALID_TOKEN` in the request attribute `authError` and continues, and the entry point uses that attribute to pick the 401 code.
- Access rules are centralized in `SecurityConfig.filterChain`: signup/login, Swagger, `/error`, `/health` are public; `POST /books`, `DELETE /books/**`, `GET /loans` require `ADMIN`; everything else requires authentication. Update this when adding endpoints. Ownership checks (e.g. only returning your own loan) are done in the service layer, throwing `ErrorCode.FORBIDDEN`.

## 규칙
- 답변은 한국어로
- API 응답은 공통 Response DTO로 감싸기
- 테스트는 JUnit5 + Mockito 사용
- 새로운 라이브러리를 설치할 때는 물어보기