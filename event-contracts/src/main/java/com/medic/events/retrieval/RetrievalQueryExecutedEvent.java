package com.medic.events.retrieval;

import java.util.UUID;

public record RetrievalQueryExecutedEvent(
        UUID queryId,
        String mode,
        int resultCount,
        long latencyMs
) {
}
