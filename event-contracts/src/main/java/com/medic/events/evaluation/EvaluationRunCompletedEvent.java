package com.medic.events.evaluation;

import java.math.BigDecimal;
import java.util.UUID;

public record EvaluationRunCompletedEvent(
        UUID runId,
        String algorithm,
        BigDecimal ndcgAtK,
        BigDecimal recallAtK,
        long latencyP95Ms
) {
}
