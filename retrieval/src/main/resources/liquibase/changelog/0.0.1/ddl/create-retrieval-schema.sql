CREATE TABLE retrieval_queries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    query_text TEXT NOT NULL,
    mode VARCHAR(32) NOT NULL,
    top_k INTEGER NOT NULL,
    latency_ms BIGINT NOT NULL,
    result_count INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_retrieval_queries_user_created_at ON retrieval_queries (user_id, created_at DESC);
