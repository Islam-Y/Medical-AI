package com.medic.evaluation.dto;

import com.medic.evaluation.entity.EvaluationRunStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EvaluationRunResponse(
        UUID id,
        String datasetName,
        String algorithm,
        int queryCount,
        EvaluationRunStatus status,
        BigDecimal recallAtK,
        BigDecimal ndcgAtK,
        BigDecimal mrr,
        long latencyP95Ms,
        long throughputQps,
        long indexSizeBytes,
        Instant createdAt,
        Instant completedAt
) {
}
