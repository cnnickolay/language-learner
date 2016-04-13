# --- !Ups

CREATE TABLE dialogue (
  id        BIGSERIAL PRIMARY KEY,
  "name"    VARCHAR(255) NOT NULL,
  course_id BIGINT       NOT NULL REFERENCES course (id)
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
  speech_id BIGINT       NOT NULL REFERENCES speech (id)
);

# --- !Downs

DROP TABLE dialogue;