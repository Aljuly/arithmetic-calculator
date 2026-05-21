-- Create the application schema on first startup.
-- Flyway migrations (V1-V3) will create all tables inside it.
-- Unquoted name: PostgreSQL stores it as lowercase 'userdb',
-- which matches what the JDBC currentSchema=UserDb parameter resolves to.
CREATE SCHEMA IF NOT EXISTS userdb;
