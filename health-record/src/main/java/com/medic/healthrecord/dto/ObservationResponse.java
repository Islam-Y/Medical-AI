package com.medic.healthrecord.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ObservationResponse(
        UUID id,
        String name,
        BigDecimal value,
        String unit,
        String referenceRange,
        Instant observedAt,
        UUID sourceDocumentId
) {
}
