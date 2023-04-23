CREATE TABLE "todos" (
  "id" integer PRIMARY KEY,
  "title" varchar
);

CREATE TABLE "tasks" (
  "id" integer PRIMARY KEY,
  "title" varchar,
  "todo_id" integer,
  "status" varchar
);

ALTER TABLE "tasks" ADD FOREIGN KEY ("todo_id") REFERENCES "todos" ("id");
