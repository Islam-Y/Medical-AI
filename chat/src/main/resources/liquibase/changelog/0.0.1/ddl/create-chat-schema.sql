CREATE TABLE chat_sessions (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    title VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_chat_sessions_user_updated_at ON chat_sessions (user_id, updated_at DESC);

CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    session_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role VARCHAR(32) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_chat_messages_session_created_at ON chat_messages (session_id, created_at ASC);

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
