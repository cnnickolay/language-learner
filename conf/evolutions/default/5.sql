# --- !Ups
ALTER TABLE media ADD COLUMN description TEXT;

# --- !Downs
ALTER TABLE media DROP COLUMN description;