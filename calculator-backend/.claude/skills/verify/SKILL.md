---
name: verify
description: Run the full test suite — backend formatting check, all backend tests (unit, integration, REST Assured), and Angular unit tests. Use before committing or when you want confidence the app is green.
disable-model-invocation: false
---

Run these from `calculator-backend/`, in order. No Postgres or Redis is needed: backend tests use an embedded H2 database (`TestDbConfig`).

1. **Formatting:**
   ```
   ./mvnw -q spotless:check
   ```
   If it fails, run `./mvnw spotless:apply`, say which files it reformatted, and continue.

2. **Backend tests:**
   ```
   ./mvnw test
   ```
   Surefire runs every `*Test.java` and `*Tests.java`: unit tests, `*PropertyTest` (plain JUnit loops, not jqwik), `*IntegrationTest`, and REST Assured tests. Don't use `verify` for more coverage: Failsafe has no `*IT` tests to run. To run a single class: `./mvnw test -Dtest=ClassName`.

3. **Angular unit tests:**
   ```
   cd ../calculator-frontend/src/main/web && NODE_OPTIONS=--openssl-legacy-provider npx ng test --watch=false --browsers=ChromeHeadless
   ```
   Angular 10 uses webpack 4, so newer Node versions need `--openssl-legacy-provider`. If `node_modules` is missing, run `npm install` first.
   The project targets Node 12. On newer Node, Karma can crash partway through (`TypeError: Cannot read properties of undefined (reading 'range')` from webpack-dev-middleware), which cuts the run short. If that happens, report it as an environment problem, not as test failures, and suggest switching to Node 12 (e.g. `nvm use 12`).

Report a clear pass/fail summary for each phase. If anything fails, show the failing test names and error messages. Do not hide failures.
