# --- !Ups
CREATE TABLE media (
  id   BIGSERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL
);

CREATE TABLE subtitle (
  id       BIGSERIAL PRIMARY KEY,
  pos      INT    NOT NULL,
  media_id BIGINT NOT NULL REFERENCES media (id)
);

CREATE TABLE word (
  id   BIGSERIAL PRIMARY KEY,
  word VARCHAR(40) NOT NULL
);

CREATE TABLE word_subtitle (
  id          BIGSERIAL PRIMARY KEY,
  time        DECIMAL,
  pos         INT    NOT NULL,
  word_id     BIGINT NOT NULL REFERENCES word (id),
  subtitle_id BIGINT NOT NULL REFERENCES subtitle (id)
);

# --- !Downs
