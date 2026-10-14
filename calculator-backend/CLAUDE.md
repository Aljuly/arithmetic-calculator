# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

The git root and Maven reactor is the parent directory (`../pom.xml`, Spring Boot 2.7.18 parent) with two modules:
- `calculator-backend/` — Spring Boot backend (this dir), base package `com.mycorp.arithmeticcalculator`
- `../calculator-frontend/` — Angular 10 frontend

`frontend-maven-plugin` lives in `../calculator-frontend/pom.xml`, so `./mvnw` inside `calculator-backend/` never builds Angular. The backend's `maven-resources-plugin` copies from a stale `tutorial-web/...` path, so the Angular bundle is not actually copied into backend resources.

## Build & Run

```bash
./mvnw clean package     # backend only (no frontend skip flag exists or is needed)
./mvnw spring-boot:run   # prefer this over java -jar (see Gotchas)
./mvnw spotless:apply    # format Java (Eclipse formatter, tabs); run before committing
```

## Testing

```bash
./mvnw test   # runs everything, including *IntegrationTest and REST Assured tests
cd ../calculator-frontend/src/main/web && ng test --watch=false
```

- Surefire includes `**/*Test.java` and `**/*Tests.java`. Failsafe is declared but no `*IT` tests exist, so `verify` adds nothing.
- `*PropertyTest.java` tests are plain JUnit 4 loops. jqwik is a declared dependency but unused.
- Tests mix JUnit 4 (vintage) and JUnit 5.

## Spring Profiles

`dev` (local Postgres `localhost:5432`, `postgres`/`passw0rd`, schema `UserDb`), `docker`, `test` (H2). No profile is active by default; with none, Boot falls back to embedded H2.

## Database Setup

```bash
cd ../docker && docker-compose up -d app-db   # needs ../docker/.env (see .env.template)
```
Don't run a plain `docker-compose up -d`: `app-backend` has `depends_on: app-redis`, but that service is commented out.

**Critical**: `spring.jpa.hibernate.ddl-auto=create` is set in every profile, so the schema is **dropped and recreated on every startup**. Flyway migrations (`src/main/resources/db/migration/`) run first, then Hibernate recreates the entity tables.

Redis is only configured in the `docker` profile. No code uses caching yet.

## Java Version

Use JDK 21. `pom.xml` declares `<java.version>21</java.version>` but `maven-compiler-plugin` targets `<release>16</release>`, so don't use Java 17+ language features.

Lombok annotation processing must be enabled in the IDE.

## Environment Variables

- Mail credentials in the properties files are hardcoded placeholders. Locally, override them with `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD`. `MAIL_USERNAME`/`MAIL_PASSWORD` exist only in compose's `.env`.
- Datasource: `SPRING_DATASOURCE_URL` / `_USERNAME` / `_PASSWORD`.

## Gotchas

- The JWT signing key is regenerated on every startup (`security/TokenService.java`), so issued tokens are invalid after a restart.
- The GeoIP DB is loaded with `ResourceUtils.getFile("classpath:maxmind/GeoLite2-City.mmdb")`, which fails when run from a packaged jar. Use `spring-boot:run` or the Docker layered image.
- Swagger UI (SpringFox 2.9.2): `/swagger-ui.html`.

## Frontend

Source is in `../calculator-frontend/src/main/web/`. Toolchain: Node 12, Angular 10, TypeScript 3.9, TSLint (no ESLint/Prettier).
- `npm start` runs an HTTPS dev server on port 443 at host `uniportal`, which needs a hosts-file entry. Use `npm run localhost` otherwise.
- `ng lint` runs TSLint.
