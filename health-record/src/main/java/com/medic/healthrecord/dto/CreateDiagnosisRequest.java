package com.medic.healthrecord.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateDiagnosisRequest(
        @NotBlank String name,
        @NotNull Instant diagnosedAt,
        String source
) {
}
