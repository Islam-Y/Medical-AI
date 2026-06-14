CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    action VARCHAR(120) NOT NULL,
    resource_type VARCHAR(120) NOT NULL,
    resource_id VARCHAR(120),
    metadata TEXT,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_audit_events_user_created_at ON audit_events (user_id, created_at DESC);
