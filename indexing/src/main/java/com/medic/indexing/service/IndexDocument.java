package com.medic.indexing.service;

import java.time.Instant;
import java.util.UUID;

public record IndexDocument(
        UUID chunkId,
        UUID userId,
        String sourceType,
        UUID sourceId,
        UUID documentId,
        UUID extractionId,
        String title,
        String content,
        String sparseTerms,
        String artifactUri,
        Integer pageNumber,
        Instant updatedAt
) {
}
