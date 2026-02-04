-- Add created_at to users to track registration date

ALTER TABLE users
  ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ;

UPDATE users
SET created_at = NOW()
WHERE created_at IS NULL;

ALTER TABLE users
  ALTER COLUMN created_at SET NOT NULL;

ALTER TABLE users
  ALTER COLUMN created_at SET DEFAULT NOW();

CREATE INDEX IF NOT EXISTS idx_users_created_at ON users (created_at);

