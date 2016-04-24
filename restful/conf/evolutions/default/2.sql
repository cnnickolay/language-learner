# --- !Ups
INSERT INTO language (id, name) VALUES
  (1, 'english'),
  (2, 'french'),
  (3, 'german');


# --- !Downs
DELETE FROM language;