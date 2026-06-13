package com.medic.events.document;

import java.math.BigDecimal;
import java.time.Instant;

public record ExtractedObservation(
        String name,
        BigDecimal value,
        String unit,
        String referenceRange,
        Instant observedAt
) {
}
