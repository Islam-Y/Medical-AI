package com.medic.audit.dto;

import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(
        UUID id,
        String action,
        String resourceType,
        String resourceId,
        String metadata,
        Instant createdAt
) {
}
