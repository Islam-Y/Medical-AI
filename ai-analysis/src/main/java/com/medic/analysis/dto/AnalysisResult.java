package com.medic.analysis.dto;

import com.medic.events.document.ExtractedObservation;

import java.util.List;

public record AnalysisResult(
        List<ExtractedObservation> observations
) {
    public static AnalysisResult empty() {
        return new AnalysisResult(List.of());
    }
}
