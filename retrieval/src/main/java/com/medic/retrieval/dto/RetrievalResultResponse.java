package com.medic.retrieval.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record RetrievalResultResponse(
        UUID sourceId,
        String sourceType,
        String title,
        String snippet,
        BigDecimal score
) {
}
