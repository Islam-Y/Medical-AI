package com.medic.auth.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthUserResponse(
        UUID id,
        String email,
        Instant createdAt
) {
}
