package com.medic.user.dto;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateUserProfileRequest(
        @Positive BigDecimal heightCm,
        @Positive BigDecimal weightKg,
        LocalDate birthDate,
        String sex,
        String knownDiagnoses
) {
}
