package com.medic.retrieval.dto;

import java.util.List;
import java.util.UUID;

public record RetrievalResponse(
        UUID queryId,
        String query,
        RetrievalMode mode,
        long latencyMs,
        List<RetrievalResultResponse> results
) {
}
