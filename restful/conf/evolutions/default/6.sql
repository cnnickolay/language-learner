# --- !Ups
ALTER TABLE media ADD CONSTRAINT media_media_group_id_fkey FOREIGN KEY (media_group_id) REFERENCES media_group (id);

CREATE TABLE user_status (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE role (
  id   BIGINT PRIMARY KEY,
  name VARCHAR(25) UNIQUE
);
COMMENT ON TABLE role IS 'roles define which RESTful resources can be invoked by certain user';

CREATE TABLE "user" (
  id               BIGSERIAL PRIMARY KEY,
  name             VARCHAR(255),
  lastname         VARCHAR(255),
  login            VARCHAR(255) NOT NULL UNIQUE,
  password_hash    VARCHAR(255) NOT NULL,
  session_duration INTEGER      NOT NULL DEFAULT 30,
  status_id        INTEGER      NOT NULL REFERENCES user_status (id),
  role_id          INTEGER      NOT NULL REFERENCES role (id),
  owner_user_id    BIGINT REFERENCES "user" (id)
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

INSERT INTO user_status (id, name) VALUES
  (1, 'active'),
  (2, 'cancelled'),
  (3, 'suspended');

INSERT INTO role VALUES (1, 'god'), (2, 'admin'), (3, 'teacher'), (4, 'student');

INSERT INTO "user" (id, name, lastname, login, password_hash, status_id, session_duration, role_id) VALUES
  (1, 'Nikolai', 'Cherkezishvili', 'god', '9ff8405a1b363859d7da5f82ba128c25d3086c0d56c2dd1249590247e1920cae', 1, 1440, 1);
SELECT setval('user_id_seq', (SELECT MAX(id)
                              FROM "user"));

ALTER TABLE media_group ADD CONSTRAINT name_key UNIQUE (name);

# --- !Downs

ALTER TABLE media_group DROP CONSTRAINT name_key;

DROP TABLE auth_token;
DROP TABLE "user";
DROP TABLE role;
DROP TABLE user_status;
