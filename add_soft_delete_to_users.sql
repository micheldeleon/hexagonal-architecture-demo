-- Soft delete support for users (logical deletion)
-- Keeps email unique (deleted users remain blocked)

ALTER TABLE users
  ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP NULL,
  ADD COLUMN IF NOT EXISTS deleted_by BIGINT NULL,
  ADD COLUMN IF NOT EXISTS delete_reason VARCHAR(500) NULL;

CREATE INDEX IF NOT EXISTS idx_users_deleted_at ON users (deleted_at);
