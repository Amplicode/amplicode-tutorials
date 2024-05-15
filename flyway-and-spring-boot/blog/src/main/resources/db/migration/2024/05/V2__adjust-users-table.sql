ALTER TABLE users DROP COLUMN fax;

UPDATE
  users 
SET
  email = 'n/a' 
WHERE
  email IS NULL;
ALTER TABLE users ALTER COLUMN  email SET NOT NULL;