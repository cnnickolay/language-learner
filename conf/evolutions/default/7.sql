# --- !Ups

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

CREATE TABLE sound_track (
  id           BIGSERIAL PRIMARY KEY,
  url          VARCHAR(255) NOT NULL,
  offset_start NUMERIC(6, 1),
  offset_end   NUMERIC(6, 1)
);
COMMENT ON TABLE sound_track IS 'a sound track which can be used by an exercise or by a chunk';
COMMENT ON COLUMN sound_track.offset_start IS 'defines an offset on a sound track file in seconds';

CREATE TABLE exercise (
  id             BIGSERIAL PRIMARY KEY,
  lesson_id      INT NOT NULL REFERENCES lesson (id),
  idx            INT NOT NULL,
  sound_track_id BIGINT REFERENCES sound_track (id),
  UNIQUE (lesson_id, idx)
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

CREATE TABLE answer (
  exercise_id BIGINT REFERENCES exercise (id),
  gap_id      BIGINT REFERENCES gap (chunk_id),
  idx         INT          NOT NULL,
  text        VARCHAR(255) NOT NULL,
  PRIMARY KEY (exercise_id, gap_id, idx, text)
);

# --- !Downs

DROP TABLE answer;
DROP TABLE gap;
DROP TABLE text;
DROP TABLE chunk;
DROP TABLE exercise;
DROP TABLE sound_track;
DROP TABLE lesson;
DROP TABLE course;