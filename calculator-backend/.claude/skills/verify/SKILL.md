---
name: verify
description: Run the full test suite — backend unit tests, integration tests, REST Assured API tests, and Angular unit tests. Use before committing or when you want confidence the app is green.
disable-model-invocation: false
---

Run all tests for this project in order:

1. **Backend tests** (unit + property-based + integration + REST Assured):
   ```
   ./mvnw verify
   ```
   Run from `calculator-backend/` (the current working directory). This runs JUnit unit tests, jqwik property-based tests, Maven Failsafe integration tests, and REST Assured API tests.

2. **Angular unit tests**:
   ```
   cd ../calculator-frontend/src/main/web && ng test --watch=false
   ```

Report a clear pass/fail summary for each phase. If anything fails, show the failing test names and error messages. Do not hide failures.

**Prerequisites**: PostgreSQL and Redis must be running (see `/dev-up`) for integration and REST Assured tests to pass against a real database.
