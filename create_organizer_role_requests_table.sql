-- Requests for granting ROLE_ORGANIZER, reviewed by admins

CREATE TABLE IF NOT EXISTS organizer_role_requests (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL REFERENCES users(id),
  status VARCHAR(20) NOT NULL,
  message VARCHAR(1000),
  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  reviewed_at TIMESTAMPTZ,
  reviewed_by BIGINT,
  review_note VARCHAR(500),
  reject_reason VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_organizer_role_requests_user_id ON organizer_role_requests (user_id);
CREATE INDEX IF NOT EXISTS idx_organizer_role_requests_status ON organizer_role_requests (status);
CREATE INDEX IF NOT EXISTS idx_organizer_role_requests_created_at ON organizer_role_requests (created_at);

