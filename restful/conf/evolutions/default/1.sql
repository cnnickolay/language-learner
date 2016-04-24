# --- !Ups
CREATE TABLE language (
  id   SERIAL PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

INSERT INTO language (id, name) VALUES
  (1, 'english'),
  (2, 'french'),
  (3, 'german');

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

CREATE TABLE course (
  id                     BIGSERIAL PRIMARY KEY,
  "name"                 VARCHAR(255) NOT NULL UNIQUE,
  target_language_id     INT          NOT NULL REFERENCES language (id),
  presenting_language_id INT          NOT NULL REFERENCES language (id)
);
COMMENT ON COLUMN course.target_language_id IS 'the language which is being studied by course';
COMMENT ON COLUMN course.presenting_language_id IS 'the language of narration for course';

CREATE TABLE lesson (
  id        BIGSERIAL PRIMARY KEY,
  "name"    VARCHAR(255) NOT NULL,
  course_id BIGINT REFERENCES course (id)
);

CREATE TABLE exercise_block (
  id         BIGSERIAL PRIMARY KEY,
  desription TEXT,
  lesson_id  BIGINT NOT NULL REFERENCES lesson (id),
  idx        INT    NOT NULL,
  UNIQUE (lesson_id, idx)
);
COMMENT ON TABLE exercise_block IS 'lesson consists of exercise blocks, which in turn consists of exercises';
COMMENT ON COLUMN exercise_block.desription IS 'should contain instructions for passing exercise block';

CREATE TABLE exercise (
  id                BIGSERIAL PRIMARY KEY,
  exercise_block_id BIGINT NOT NULL REFERENCES exercise_block (id),
  idx               INT    NOT NULL,
  UNIQUE (exercise_block_id, idx)
);

CREATE TABLE chunk (
  id          BIGSERIAL PRIMARY KEY,
  exercise_id BIGINT NOT NULL REFERENCES exercise (id),
  idx         INT    NOT NULL,
  UNIQUE (exercise_id, idx)
);

CREATE TABLE text (
  chunk_id BIGINT PRIMARY KEY    NOT NULL REFERENCES chunk (id) ON DELETE CASCADE,
  text     VARCHAR(255)          NOT NULL
);

CREATE TABLE gap (
  chunk_id BIGINT PRIMARY KEY NOT NULL REFERENCES chunk (id) ON DELETE CASCADE,
  text     VARCHAR(255)
);
COMMENT ON TABLE gap IS 'missing part in an exercise which student has to fill in to test his knowledge';
COMMENT ON COLUMN gap.text IS 'default answer, can be used in case if there is only one correct answer for the gap, otherwise answer table should be used';

CREATE TABLE gap_answer (
  gap_id BIGINT REFERENCES gap (chunk_id),
  text   VARCHAR(255) NOT NULL,
  PRIMARY KEY (gap_id, text)
);
COMMENT ON TABLE gap_answer IS 'answers for the gap, this table should be used only if there are many possible answers for the gap';

-- ---------- DIALOGUES
CREATE TABLE sound_track (
  id  BIGSERIAL PRIMARY KEY,
  url VARCHAR(255) NOT NULL
);

CREATE TABLE dialogue (
  id             BIGSERIAL PRIMARY KEY,
  "name"         VARCHAR(255) NOT NULL,
  sound_track_id BIGINT REFERENCES sound_track (id)
);

CREATE TABLE speech (
  id          BIGSERIAL PRIMARY KEY,
  speaker     VARCHAR(50),
  idx         INT NOT NULL,
  dialogue_id BIGINT REFERENCES dialogue (id),
  UNIQUE (dialogue_id, idx)
);

CREATE TABLE phrase (
  id        BIGSERIAL PRIMARY KEY,
  text      VARCHAR(255) NOT NULL,
  "offset"  NUMERIC(6, 1),
  speech_id BIGINT       NOT NULL REFERENCES speech (id)
);


# --- !Downs
DROP TABLE phrase;
DROP TABLE speech;
DROP TABLE dialogue;
DROP TABLE sound_track;
DROP TABLE gap_answer;
DROP TABLE gap;
DROP TABLE text;
DROP TABLE chunk;
DROP TABLE exercise;
DROP TABLE exercise_block;
DROP TABLE lesson;
DROP TABLE course;
DROP TABLE auth_token;
DROP TABLE "user";
DROP TABLE role;
DROP TABLE user_status;
DELETE FROM language;
DROP TABLE language;
