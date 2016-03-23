# --- !Ups

CREATE TABLE user_status (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

CREATE TABLE "user" (
  id             BIGSERIAL PRIMARY KEY,
  name           VARCHAR(255),
  lastname       VARCHAR(255),
  login          VARCHAR(255) NOT NULL,
  passwordHash   VARCHAR(255) NOT NULL,
  user_status_id INTEGER      NOT NULL REFERENCES user_status (id)
);
COMMENT ON COLUMN "user".passwordHash IS 'SHA256 encrypted password';

CREATE TABLE auth_token (
  token      VARCHAR(255),
  created_at TIMESTAMP,
  expires_at TIMESTAMP,
  active     BOOLEAN,
  user_id    BIGINT REFERENCES "user" (id) NOT NULL
);

CREATE TABLE role (
  name VARCHAR(25) PRIMARY KEY
);
COMMENT ON TABLE role is 'roles define which RESTful resources can be invoked by certain user';

CREATE TABLE user_role (
  user_id BIGINT REFERENCES "user" (id),
  role    VARCHAR(25) REFERENCES role (name)
);

INSERT INTO user_status (id, name) VALUES
  (1, 'active'),
  (2, 'cancelled'),
  (3, 'suspended');

INSERT INTO role VALUES ('god'), ('admin'), ('teacher'), ('student');

INSERT INTO "user" (id, name, lastname, login, passwordHash, user_status_id) VALUES
  (1, 'Nikolai', 'Cherkezishvili', 'god', '9ff8405a1b363859d7da5f82ba128c25d3086c0d56c2dd1249590247e1920cae', 1);
SELECT setval('user_id_seq', (SELECT MAX(id) from "user"));

INSERT INTO user_role (user_id, role) VALUES (1, 'god');