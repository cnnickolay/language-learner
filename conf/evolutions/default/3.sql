# --- !Ups
ALTER TABLE subtitle ALTER COLUMN "offset" TYPE NUMERIC(6, 1);

# --- !Downs
ALTER TABLE subtitle ALTER COLUMN "offset" TYPE DECIMAL;