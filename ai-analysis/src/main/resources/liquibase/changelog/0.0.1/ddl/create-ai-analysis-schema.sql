CREATE TABLE analysis_jobs (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    file_name VARCHAR(512) NOT NULL,
    content_type VARCHAR(255) NOT NULL,
    storage_bucket VARCHAR(255) NOT NULL,
    storage_key TEXT NOT NULL,
    artifact_bucket VARCHAR(255),
    artifact_key TEXT,
    layout_artifact_bucket VARCHAR(255),
    layout_artifact_key TEXT,
    model_name VARCHAR(120),
    model_version VARCHAR(120),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_analysis_jobs_user_created_at ON analysis_jobs (user_id, created_at DESC);

CREATE TABLE outbox_events (
    id UUID PRIMARY KEY,
    topic VARCHAR(255) NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_pending ON outbox_events (created_at) WHERE published_at IS NULL;
