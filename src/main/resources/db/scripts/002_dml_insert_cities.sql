-- liquibase formatted sql
-- changeset liquibase:002
insert into cities(name) values('Москва');
insert into cities(name) values('Санкт-Петербург');
insert into cities(name) values('Екатеринбург');
-- rollback delete from cities testTable where name='Санкт-Петербург'
-- rollback delete from cities testTable where name='Москва'
-- rollback delete from cities testTable where name='Екатеринбург'
