package com.medic.document.dto;

import com.medic.document.entity.DocumentStatus;

import java.time.Instant;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String originalFileName,
        String contentType,
        long sizeBytes,
        DocumentStatus status,
        Instant createdAt
) {
}
