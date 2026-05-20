---
name: dev-up
description: Start all local development services (PostgreSQL, Redis) required to run the app. Run this before ./mvnw spring-boot:run or ./mvnw verify.
disable-model-invocation: true
---

Start local development infrastructure for this project.

1. **Start PostgreSQL** via Docker Compose:
   ```
   cd docker && docker-compose up -d
   ```
   This starts PostgreSQL on port 5432.

2. **Start Redis** (not in docker-compose, run separately):
   ```
   docker run -d --name calculator-redis -p 6379:6379 redis:latest
   ```
   Skip this if a Redis container named `calculator-redis` is already running.

3. **Verify services are up**:
   ```
   docker ps
   ```
   Confirm `app-db` (PostgreSQL) and `calculator-redis` containers show as running.

4. Report which services started successfully and which were already running. Warn if either service failed to start.
