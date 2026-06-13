package com.medic.analysis.dto;

import com.medic.analysis.entity.AnalysisJobStatus;

import java.time.Instant;
import java.util.UUID;

public record AnalysisJobResponse(
        UUID id,
        UUID documentId,
        String fileName,
        String contentType,
        AnalysisJobStatus status,
        Instant createdAt,
        Instant completedAt
) {
}
