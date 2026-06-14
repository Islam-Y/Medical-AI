package com.medic.indexing.dto;

import java.time.Instant;
import java.util.UUID;

public record IndexEntryResponse(
        UUID id,
        String sourceType,
        UUID sourceId,
        String title,
        String sparseTerms,
        Instant updatedAt
) {
}
