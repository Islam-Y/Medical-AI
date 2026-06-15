CREATE TABLE index_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    source_type VARCHAR(120) NOT NULL,
    source_id UUID NOT NULL,
    chunk_id UUID NOT NULL,
    document_id UUID,
    extraction_id UUID,
    artifact_bucket VARCHAR(255),
    artifact_key TEXT,
    page_number INTEGER,
    title VARCHAR(512) NOT NULL,
    content TEXT NOT NULL,
    sparse_terms TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE UNIQUE INDEX idx_index_entries_source ON index_entries (user_id, source_type, source_id);
CREATE UNIQUE INDEX idx_index_entries_chunk ON index_entries (chunk_id);
CREATE INDEX idx_index_entries_user_updated_at ON index_entries (user_id, updated_at DESC);
