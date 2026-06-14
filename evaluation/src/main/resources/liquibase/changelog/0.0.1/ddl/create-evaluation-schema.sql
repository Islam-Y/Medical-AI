CREATE TABLE evaluation_runs (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    dataset_name VARCHAR(255) NOT NULL,
    algorithm VARCHAR(120) NOT NULL,
    query_count INTEGER NOT NULL,
    status VARCHAR(32) NOT NULL,
    recall_atk NUMERIC(8, 4),
    ndcg_atk NUMERIC(8, 4),
    mrr NUMERIC(8, 4),
    latency_p95ms BIGINT NOT NULL,
    throughput_qps BIGINT NOT NULL,
    index_size_bytes BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    completed_at TIMESTAMPTZ
);

CREATE INDEX idx_evaluation_runs_user_created_at ON evaluation_runs (user_id, created_at DESC);
