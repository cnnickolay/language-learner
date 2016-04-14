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