package com.medic.healthrecord.dto;

import java.time.Instant;
import java.util.UUID;

public record SymptomResponse(
        UUID id,
        String name,
        int intensity,
        String notes,
        Instant observedAt
) {
}
