package com.medic.consent.dto;

import com.medic.consent.entity.ConsentScope;
import com.medic.consent.entity.ConsentStatus;

import java.time.Instant;
import java.util.UUID;

public record ConsentResponse(
        UUID id,
        ConsentScope scope,
        ConsentStatus status,
        String subject,
        Instant grantedAt,
        Instant revokedAt
) {
}
