# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a two-module project:
- `calculator-backend/` — Spring Boot 2.7.18 backend (this repo)
- `../calculator-frontend/` — Angular 10 frontend (sibling directory)

The Maven build in this backend uses `frontend-maven-plugin` to compile and bundle the Angular app into backend resources.

## Build & Run

```bash
# Backend only (skips frontend build)
./mvnw clean package -Dskip.frontend=true

# Full build (backend + Angular)
./mvnw clean package

# Run locally
./mvnw spring-boot:run
```

## Testing

```bash
# Unit tests + jqwik property-based tests
./mvnw test

# Unit + integration tests (Failsafe) + REST Assured
./mvnw verify

# Angular unit tests (run from frontend web dir)
cd ../calculator-frontend/src/main/web && ng test --watch=false
```

Property-based tests using jqwik are in `src/test/java/com/mycorp/arithmeticcalculator/` — they test token expiry, validation, and registration invariants.

## Database Setup

PostgreSQL runs via Docker Compose:
```bash
cd docker && docker-compose up -d
```
This starts PostgreSQL on port 5432.

**Critical**: `spring.jpa.hibernate.ddl-auto=create` is the default — the schema is **dropped and recreated on every startup**. Do not run against a database with data you want to keep unless this is overridden.

Flyway migrations are in `src/main/resources/db/migration/`. They run after Hibernate DDL creation.

Redis is required for caching but is not in `docker/docker-compose.yml`. Start it separately:
```bash
docker run -d -p 6379:6379 redis:latest
```

## Java Version

Use JDK 21 (LTS). The `pom.xml` declares `<java.version>25</java.version>` but the Maven compiler plugin targets `release 16`. Stick to JDK 21 locally to avoid surprises.

## IDE Requirements

Lombok annotation processing must be enabled (IntelliJ: *Settings → Build → Compiler → Annotation Processors → Enable*). Without it, all Lombok-generated methods show as errors.

## Frontend Development

Angular source lives in `../calculator-frontend/src/main/web/`. Commands:
```bash
cd ../calculator-frontend/src/main/web

npm start          # HTTPS dev server on port 443
npm run localhost  # Alternative dev server
ng test            # Unit tests (Karma/Jasmine)
ng lint            # TSLint (deprecated but configured)
```

## Environment Variables

The app requires these to be set for email and external services:
- `MAIL_USERNAME` / `MAIL_PASSWORD` — Gmail SMTP credentials
- Database URL/credentials can be overridden via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

## Key Libraries

- **Security**: Spring Security + JJWT 0.12.5 (JWT), Passay (password policy), AeroGear OTP (2FA)
- **Persistence**: Spring Data JPA + Flyway + PostgreSQL; H2 for tests
- **Caching**: Spring Data Redis
- **Docs**: SpringFox Swagger 2.9.2 (available at `/swagger-ui.html` when running)
- **Geo/UA**: MaxMind GeoIP2 + UA-Parser for request metadata
