CREATE TABLE observations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    value NUMERIC(19, 6) NOT NULL,
    unit VARCHAR(64) NOT NULL,
    reference_range VARCHAR(255),
    observed_at TIMESTAMPTZ NOT NULL,
    source_document_id UUID,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_observations_user_observed_at ON observations (user_id, observed_at DESC);

CREATE TABLE symptoms (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    intensity INTEGER NOT NULL,
    notes TEXT,
    observed_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_symptoms_user_observed_at ON symptoms (user_id, observed_at DESC);

CREATE TABLE diagnoses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    diagnosed_at TIMESTAMPTZ NOT NULL,
    source VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_diagnoses_user_diagnosed_at ON diagnoses (user_id, diagnosed_at DESC);

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
