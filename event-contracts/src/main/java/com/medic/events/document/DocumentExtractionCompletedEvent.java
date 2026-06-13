package com.medic.events.document;

import java.util.List;
import java.util.UUID;

public record DocumentExtractionCompletedEvent(
        UUID documentId,
        String status,
        List<ExtractedObservation> observations
) {
}
