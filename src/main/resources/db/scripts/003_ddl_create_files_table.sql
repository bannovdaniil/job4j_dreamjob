-- liquibase formatted sql
-- changeset liquibase:003
create table files
(
    id   serial primary key,
    name varchar not null,
    path varchar not null unique
);
-- rollback DROP TABLE files;