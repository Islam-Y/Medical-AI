package com.medic.document.service;

import com.medic.document.dto.AnalysisResult;

import java.nio.file.Path;
import java.util.UUID;

public interface AnalysisClient {

    AnalysisResult analyze(UUID documentId, Path filePath, String contentType);
}
