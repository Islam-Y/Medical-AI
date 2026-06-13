package com.medic.user.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID userId,
        String email,
        BigDecimal heightCm,
        BigDecimal weightKg,
        LocalDate birthDate,
        String sex,
        String knownDiagnoses,
        Instant createdAt,
        Instant updatedAt
) {
}
