# --- !Ups
CREATE TABLE media (
  id        BIGSERIAL PRIMARY KEY,
  name      VARCHAR(100) NOT NULL,
  media_url VARCHAR(255) NOT NULL
);

CREATE TABLE subtitle (
  id       BIGSERIAL PRIMARY KEY,
  "offset" DECIMAL,
  "text"   TEXT    NOT NULL,
  media_id BIGINT  NOT NULL REFERENCES media (id)
);

# --- !Downs
DROP TABLE subtitle;
DROP TABLE media;