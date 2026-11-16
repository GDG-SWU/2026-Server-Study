# Project guidance

## Project

Library REST API study project using Java 17, Spring Boot 3.3, Gradle,
Spring Data JPA, Spring Security, BCrypt, JWT and Swagger.
Keep user-facing messages and code comments in Korean.
Follow AGENTS.md for repository preferences and remote push authorization.

## Commands

```powershell
.\gradlew.bat test bootJar
.\gradlew.bat bootRun
.\gradlew.bat bootRun --args='--spring.profiles.active=dev'
```

Work uses weekly branches such as `week-06/sua`.
Creating a branch or commit does not authorize a remote push.

## Environment and profiles

- Default MySQL configuration uses `compose.yaml` with MySQL 8.4.
- Set `DB_PASSWORD` and `JWT_SECRET` in the application environment.
- Start MySQL explicitly with `docker compose up -d --wait` before running the app.
- `DB_PORT` optionally overrides local port 3306 for both Compose and the app.
- `.env` is ignored by Git and is read by Compose; pass variables to IntelliJ separately.
- `dev` uses an in-memory H2 database and enables the local H2 console.
- `prod` uses `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, port 5000, SQL logging off,
  and Docker Compose integration disabled. Activate with `--spring.profiles.active=prod`.
- JWT HMAC keys must contain at least 32 UTF-8 bytes.
- `GET /health` is public and returns an `ApiResponse` containing `"ok"`.

## Structure

Package root is `com.example.server_study_2026`.
Main Java file paths follow `week-06/sebin`.

- `api/<feature>` contains controllers and `dto` request/response records.
- `service/<feature>` contains transactional business logic.
- `domain/<feature>` contains entities, roles and Spring Data repositories.
- `global/common/BaseEntity` provides audited `createdAt` for User.
- `global/response` contains the shared response envelope.
- `global/exception` contains error codes and exception handling.
- `global/security` contains JWT, role checks and local security configuration.
- `global/health` contains the public health endpoint.

## Existing contracts

- Keep existing API URLs, JSON fields and published OpenAPI schema names stable.
- Responses use `ApiResponse(success, code, message, data)`.
- Use `BusinessException(ErrorCode)` for business errors.
- Signup always creates USER, hashes passwords with BCrypt and rejects duplicate emails.
- The token principal is `JwtProvider.LoginUser`, containing the user ID and role.
- Books can be created/deleted only by ADMIN. Other existing protected APIs require login.
- Loan creation uses the authenticated user ID; another member cannot return the loan.
- Retain regression tests for concurrency, API errors, authentication and authorization.
- Tests use isolated H2 configuration through `ApiTestSupport` and do not require Docker.
- Keep Spring Boot 3.3-compatible dependencies; springdoc 3.x belongs to the reference
  project's Spring Boot 4.x setup and must not be copied independently.
