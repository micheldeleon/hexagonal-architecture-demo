-- Soft moderation for tournaments (admin deactivation)
-- Adds moderation fields without deleting any tournament data.

ALTER TABLE public.tournaments
    ADD COLUMN IF NOT EXISTS moderation_status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS moderated_at TIMESTAMPTZ NULL,
    ADD COLUMN IF NOT EXISTS moderated_by_admin_id BIGINT NULL,
    ADD COLUMN IF NOT EXISTS moderation_reason TEXT NULL;

-- Backfill safety (in case the column existed without default)
UPDATE public.tournaments
SET moderation_status = 'ACTIVE'
WHERE moderation_status IS NULL;

-- Optional index to speed up public listings
CREATE INDEX IF NOT EXISTS idx_tournaments_moderation_status
    ON public.tournaments (moderation_status);
