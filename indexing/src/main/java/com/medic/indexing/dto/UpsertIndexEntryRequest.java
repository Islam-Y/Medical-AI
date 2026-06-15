package com.medic.indexing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpsertIndexEntryRequest(
        @NotBlank String sourceType,
        @NotNull UUID sourceId,
        UUID documentId,
        UUID extractionId,
        String artifactBucket,
        String artifactKey,
        Integer pageNumber,
        @NotBlank String title,
        @NotBlank String content
) {
}
