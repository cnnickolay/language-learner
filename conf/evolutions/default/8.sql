# --- !Ups

CREATE TABLE user_status (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL
);

CREATE TABLE "user" (
  id                       BIGSERIAL PRIMARY KEY,
  name                     VARCHAR(255),
  lastname                 VARCHAR(255),
  login                    VARCHAR(255) NOT NULL,
  password_hash            VARCHAR(255) NOT NULL,
  session_duration INTEGER      NOT NULL DEFAULT 30,
  user_status_id           INTEGER      NOT NULL REFERENCES user_status (id)
);
COMMENT ON COLUMN "user".password_hash IS 'SHA256 encrypted password';
COMMENT ON COLUMN "user".session_duration IS 'defines the maximum amount of minutes user session can last without being refreshed';

CREATE TABLE auth_token (
  token      VARCHAR(255) PRIMARY KEY,
  created_at TIMESTAMP                     NOT NULL,
  expires_at TIMESTAMP                     NOT NULL,
  expired_at TIMESTAMP,
  active     BOOLEAN                       NOT NULL DEFAULT FALSE,
  user_id    BIGINT REFERENCES "user" (id) NOT NULL
);
COMMENT ON COLUMN auth_token.expired_at IS 'when token was marked as expired';

CREATE TABLE role (
  name VARCHAR(25) PRIMARY KEY
);
COMMENT ON TABLE role IS 'roles define which RESTful resources can be invoked by certain user';

CREATE TABLE user_role (
  user_id BIGINT REFERENCES "user" (id),
  role    VARCHAR(25) REFERENCES role (name)
);

INSERT INTO user_status (id, name) VALUES
  (1, 'active'),
  (2, 'cancelled'),
  (3, 'suspended');

INSERT INTO role VALUES ('god'), ('admin'), ('teacher'), ('student');

INSERT INTO "user" (id, name, lastname, login, password_hash, user_status_id, session_duration) VALUES
  (1, 'Nikolai', 'Cherkezishvili', 'god', '9ff8405a1b363859d7da5f82ba128c25d3086c0d56c2dd1249590247e1920cae', 1, 1440);
SELECT setval('user_id_seq', (SELECT MAX(id)
                              FROM "user"));

INSERT INTO user_role (user_id, role) VALUES (1, 'god');