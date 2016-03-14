# --- !Ups
CREATE TABLE media_group (
  id          BIGSERIAL PRIMARY KEY,
  name        VARCHAR(255) NOT NULL,
  description TEXT,
  language_id SERIAL       NOT NULL REFERENCES language (id)
);

ALTER TABLE media DROP COLUMN language_id;
ALTER TABLE media ADD COLUMN media_group_id BIGINT;

INSERT INTO media_group (id, name, description, language_id) VALUES
  (1, 'Grammaire en dialogues', NULL, 2),
  (2, 'Futurama', NULL, 2),
  (3, 'Father ted', NULL, 1);

SELECT setval('media_group_id_seq', (SELECT MAX(id) from "media_group"));

# --- !Downs
ALTER TABLE media DROP COLUMN media_group_id;
ALTER TABLE media ADD COLUMN language_id INT NOT NULL DEFAULT 2;
DROP TABLE media_group;