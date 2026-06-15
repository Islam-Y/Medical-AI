package com.medic.analysis.dto;

import com.medic.events.document.ExtractedObservation;

import java.util.List;

public record AnalysisResult(
        String modelName,
        String modelVersion,
        String canonicalMarkdown,
        String layoutJson,
        List<ExtractedObservation> observations
) {
    public static AnalysisResult empty() {
        return new AnalysisResult("stub-analysis", "0.0.1", "", "{\"pages\":[]}", List.of());
    }
}
