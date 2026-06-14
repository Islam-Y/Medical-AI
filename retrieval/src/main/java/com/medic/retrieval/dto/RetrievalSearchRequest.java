package com.medic.retrieval.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RetrievalSearchRequest(
        @NotBlank String query,
        @NotNull RetrievalMode mode,
        @Min(1) @Max(50) int topK
) {
}
