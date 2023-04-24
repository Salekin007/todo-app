CREATE SCHEMA IF NOT EXISTS todo_schema;

GRANT ALL PRIVILEGES ON DATABASE todo_db TO todouser;

SET search_path TO todo_schema;

-- auto-generated definition
create table todos
(
    id    serial
        primary key,
    title varchar
);

alter table todos
    owner to todouser;


-- auto-generated definition
create table tasks
(
    id      serial
        primary key,
    title   varchar,
    todo_id integer
        references todos,
    status  varchar
);

alter table tasks
    owner to todouser;

-- auto-generated definition
create sequence todos_id_seq
    as integer;

alter sequence todos_id_seq owner to todouser;

alter sequence todos_id_seq owned by todos.id;


-- auto-generated definition
create sequence tasks_id_seq
    as integer;

alter sequence tasks_id_seq owner to todouser;

alter sequence tasks_id_seq owned by tasks.id;