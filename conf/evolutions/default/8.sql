# --- !Ups

CREATE TABLE "user" (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255),
  lastname     VARCHAR(255),
  login        VARCHAR(255) NOT NULL,
  passwordHash VARCHAR(255) NOT NULL
);

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

CREATE TABLE user_role (
  user_id BIGINT REFERENCES "user" (id),
  role    VARCHAR(25) REFERENCES role (name)
);

INSERT INTO role VALUES ('admin'), ('teacher'), ('student');
INSERT INTO "user" (id, name, lastname, login, passwordHash)
  VALUES (1, 'Nikolai', 'Cherkezishvili', 'admin', '9ff8405a1b363859d7da5f82ba128c25d3086c0d56c2dd1249590247e1920cae');
INSERT INTO user_role (user_id, role) VALUES (1, 'admin');
