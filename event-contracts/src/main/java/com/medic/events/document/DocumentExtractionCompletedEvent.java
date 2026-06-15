package com.medic.events.document;

import java.util.List;
import java.util.UUID;

public record DocumentExtractionCompletedEvent(
        UUID documentId,
        UUID extractionId,
        String status,
        String artifactBucket,
        String artifactKey,
        String artifactContentType,
        String modelName,
        String modelVersion,
        List<ExtractedObservation> observations
) {
}
