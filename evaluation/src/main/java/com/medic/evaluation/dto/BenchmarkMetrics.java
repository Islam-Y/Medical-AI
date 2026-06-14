package com.medic.evaluation.dto;

import java.math.BigDecimal;

public record BenchmarkMetrics(
        BigDecimal recallAtK,
        BigDecimal ndcgAtK,
        BigDecimal mrr,
        long latencyP95Ms,
        long throughputQps,
        long indexSizeBytes
) {
}
