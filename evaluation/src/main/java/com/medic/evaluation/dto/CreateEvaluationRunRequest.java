package com.medic.evaluation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateEvaluationRunRequest(
        @NotBlank String datasetName,
        @NotBlank String algorithm,
        @Min(1) @Max(100000) int queryCount
) {
}
