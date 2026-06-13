package com.medic.healthrecord.dto;

import java.time.Instant;
import java.util.UUID;

public record TimelineItemResponse(
        String type,
        UUID id,
        String title,
        Instant occurredAt
) {
}
