FROM postgres:16-alpine

# Credentials are injected at runtime via docker-compose environment variables.
# init.sql runs once on first container startup to create the application schema.
COPY init.sql /docker-entrypoint-initdb.d/01_init.sql
