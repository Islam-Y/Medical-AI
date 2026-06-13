package com.medic.healthrecord.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateSymptomRequest(
        @NotBlank String name,
        @Min(1) @Max(10) int intensity,
        String notes,
        @NotNull Instant observedAt
) {
}
