package com.medic.analysis.service;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StubAnalysisClientTest {

    @Test
    void returnsEmptyAnalysisResult() {
        // Arrange
        StubAnalysisClient client = new StubAnalysisClient();

        // Act
        var result = client.analyze(UUID.randomUUID(), Path.of("/tmp/labs.pdf"), "application/pdf");

        // Assert
        assertThat(result.observations()).isEmpty();
    }
}
