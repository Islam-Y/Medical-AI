package com.medic.analysis.service;

import java.util.UUID;

public record AnalysisInput(
        UUID documentId,
        String fileName,
        String contentType,
        String storageBucket,
        String storageKey,
        StoredObjectContent originalObject
) {
}
