package com.medic.analysis.service;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StubAnalysisClientTest {

    @Test
    void returnsEmptyAnalysisResult() {
        // Arrange
        StubAnalysisClient client = new StubAnalysisClient();
        UUID documentId = UUID.randomUUID();
        AnalysisInput input = new AnalysisInput(
                documentId,
                "labs.pdf",
                "application/pdf",
                "medical-ai-documents",
                "documents/key",
                new StoredObjectContent("medical-ai-documents", "documents/key", "application/pdf", 7, new ByteArrayInputStream("content".getBytes()))
        );

        // Act
        var result = client.analyze(input);

        // Assert
        assertThat(result.observations()).isEmpty();
        assertThat(result.canonicalMarkdown()).contains(documentId.toString());
    }
}
