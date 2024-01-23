-- liquibase formatted sql
-- changeset liquibase:001
CREATE TABLE cities
(
    id   SERIAL PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);
-- rollback DROP TABLE cities;
