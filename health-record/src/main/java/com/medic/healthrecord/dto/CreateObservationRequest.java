package com.medic.healthrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateObservationRequest(
        @NotBlank String name,
        @NotNull BigDecimal value,
        @NotBlank String unit,
        String referenceRange,
        @NotNull Instant observedAt,
        UUID sourceDocumentId
) {
}
