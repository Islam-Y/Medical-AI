package com.medic.document.service;

import com.medic.document.dto.AnalysisResult;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.UUID;

@Component
public class StubAnalysisClient implements AnalysisClient {

    @Override
    public AnalysisResult analyze(UUID documentId, Path filePath, String contentType) {
        return AnalysisResult.empty();
    }
}
