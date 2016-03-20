# --- !Ups
CREATE OR REPLACE FUNCTION undefined_media_group() RETURNS VOID AS
$$
  declare
    undefined_id bigint;;
  begin
    INSERT INTO media_group (id, "name", description, language_id) VALUES (0, 'undefined', '', 1);;
    SELECT id into undefined_id FROM media_group WHERE name = 'undefined';;
    UPDATE media SET media_group_id = undefined_id WHERE media_group_id IS NULL;;
  end;;
$$
LANGUAGE plpgsql;

do $$
begin
  PERFORM undefined_media_group();;
end;;
$$ LANGUAGE plpgsql;

DROP FUNCTION IF EXISTS undefined_media_group();

ALTER TABLE media ALTER COLUMN media_group_id SET NOT NULL;