package com.medic.analysis.controller;

import com.medic.analysis.dto.AnalysisJobResponse;
import com.medic.analysis.entity.AnalysisJobStatus;
import com.medic.analysis.service.AnalysisJobService;
import com.medic.analysis.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnalysisJobControllerTest {

    private final AnalysisJobService analysisJobService = mock(AnalysisJobService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AnalysisJobController controller = new AnalysisJobController(analysisJobService, jwtService);

    @Test
    void jobsUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        AnalysisJobResponse expected = new AnalysisJobResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "labs.pdf",
                "application/pdf",
                AnalysisJobStatus.COMPLETED,
                "stub-analysis",
                "0.0.1",
                "s3://medical-ai-extractions/artifact.md",
                Instant.now(),
                Instant.now()
        );
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(analysisJobService.jobs(userId)).thenReturn(List.of(expected));

        // Act
        List<AnalysisJobResponse> response = controller.jobs("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(analysisJobService).jobs(userId);
    }

    @Test
    void jobUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        AnalysisJobResponse expected = new AnalysisJobResponse(
                jobId,
                UUID.randomUUID(),
                "labs.pdf",
                "application/pdf",
                AnalysisJobStatus.PENDING,
                null,
                null,
                null,
                Instant.now(),
                null
        );
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(analysisJobService.job(userId, jobId)).thenReturn(expected);

        // Act
        AnalysisJobResponse response = controller.job("Bearer token", jobId);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(analysisJobService).job(userId, jobId);
    }
}
