CREATE TABLE consents (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    scope VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    subject VARCHAR(255),
    granted_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ
);

CREATE INDEX idx_consents_user_granted_at ON consents (user_id, granted_at DESC);
