-- liquibase formatted sql
-- changeset liquibase:001
create table cities
(
    id   serial primary key,
    name varchar not null unique
);
