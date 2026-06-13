package com.medic.events.document;

import java.util.UUID;

public record DocumentUploadedEvent(
        UUID documentId,
        String fileName,
        String contentType
) {
}
