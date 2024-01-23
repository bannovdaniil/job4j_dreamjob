-- liquibase formatted sql
-- changeset liquibase:005
CREATE TABLE candidates
(
    id            SERIAL PRIMARY KEY,
    name          VARCHAR NOT NULL,
    description   VARCHAR NOT NULL,
    creation_date TIMESTAMP
);
-- rollback DROP TABLE candidates;