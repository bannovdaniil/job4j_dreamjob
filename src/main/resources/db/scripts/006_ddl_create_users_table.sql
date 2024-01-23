-- liquibase formatted sql
-- changeset liquibase:006
CREATE TABLE users
(
    id       SERIAL PRIMARY KEY,
    email    VARCHAR NOT NULL UNIQUE,
    name     VARCHAR NOT NULL,
    password VARCHAR NOT NULL
);
-- rollback DROP TABLE users;