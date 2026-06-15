package com.medic.analysis.service;

import com.medic.analysis.dto.AnalysisResult;
import org.springframework.stereotype.Component;

@Component
public class StubAnalysisClient implements AnalysisClient {

    @Override
    public AnalysisResult analyze(AnalysisInput input) {
        String markdown = """
                # Canonical Medical Document

                - Document ID: %s
                - File name: %s
                - Content type: %s
                - Source object: s3://%s/%s

                This is a stub extraction artifact. Replace this adapter with OCR/VLM/LLM extraction.
                """.formatted(
                input.documentId(),
                input.fileName(),
                input.contentType(),
                input.storageBucket(),
                input.storageKey()
        );
        String layoutJson = """
                {"documentId":"%s","sourceObject":"s3://%s/%s","pages":[]}
                """.formatted(input.documentId(), input.storageBucket(), input.storageKey());
        return new AnalysisResult("stub-analysis", "0.0.1", markdown, layoutJson, java.util.List.of());
    }
}
