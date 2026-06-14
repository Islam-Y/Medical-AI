package com.medic.analysis.service;

import com.medic.analysis.dto.AnalysisResult;

import java.nio.file.Path;
import java.util.UUID;

public interface AnalysisClient {

    AnalysisResult analyze(UUID documentId, Path filePath, String contentType);
}
