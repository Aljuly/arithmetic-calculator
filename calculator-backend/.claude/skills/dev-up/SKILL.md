---
name: dev-up
description: Start local PostgreSQL (and optionally Redis) so the backend can run with the dev profile. Run this before `./mvnw spring-boot:run`. Not needed for tests, which use embedded H2.
disable-model-invocation: true
---

Start local development infrastructure. The compose file is at the repo root: `../docker/docker-compose.yml` relative to `calculator-backend/`.

1. **Check `../docker/.env` exists.** Compose reads `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB`, `MAIL_USERNAME`, `MAIL_PASSWORD` from it. If it's missing, copy `../docker/.env.template` to `../docker/.env` and tell the user to fill in real values. Don't print the contents of `.env`.

2. **Start only PostgreSQL:**
   ```
   cd ../docker && docker-compose up -d app-db
   ```
   Do NOT run a plain `docker-compose up -d`: `app-backend` has `depends_on: app-redis`, but that service is commented out, so the full stack won't start. `app-db` is built from `../docker/db` and its `init.sql` creates the `userdb` schema on first startup. It listens on port 5432.

3. **Redis is optional.** Only the `docker` profile configures Redis, and no code uses caching. Start it only if the user asks:
   ```
   docker run -d --name calculator-redis -p 6379:6379 redis:7-alpine
   ```
   If a `calculator-redis` container exists but is stopped, run `docker start calculator-redis` instead.

4. **Wait for Postgres to be healthy:** run `docker-compose ps app-db` from `../docker` until the status shows `healthy` or `running`.

5. **Report** which services started, which were already running, and any failures. Remind the user:
   - Run the app with the `dev` profile: `SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run` (with no profile, the app falls back to embedded H2 and ignores Postgres).
   - The `dev` profile connects as `postgres`/`passw0rd`. If `POSTGRES_PASSWORD` in `.env` differs, set `SPRING_DATASOURCE_PASSWORD` to match.
   - `ddl-auto=create` drops and recreates the schema on every startup.
