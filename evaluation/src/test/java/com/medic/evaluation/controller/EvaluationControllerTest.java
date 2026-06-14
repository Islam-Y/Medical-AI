package com.medic.evaluation.controller;

import com.medic.evaluation.dto.CreateEvaluationRunRequest;
import com.medic.evaluation.dto.EvaluationRunResponse;
import com.medic.evaluation.entity.EvaluationRunStatus;
import com.medic.evaluation.service.EvaluationService;
import com.medic.evaluation.service.JwtService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EvaluationControllerTest {

    private final EvaluationService evaluationService = mock(EvaluationService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final EvaluationController controller = new EvaluationController(evaluationService, jwtService);

    @Test
    void createRunUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateEvaluationRunRequest request = new CreateEvaluationRunRequest("medical-synthetic", "HYBRID", 100);
        EvaluationRunResponse expected = response(UUID.randomUUID());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(evaluationService.createRun(userId, request)).thenReturn(expected);

        // Act
        EvaluationRunResponse response = controller.createRun("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(evaluationService).createRun(userId, request);
    }

    @Test
    void runsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        EvaluationRunResponse expected = response(UUID.randomUUID());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(evaluationService.runs(userId)).thenReturn(List.of(expected));

        // Act
        List<EvaluationRunResponse> response = controller.runs("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(evaluationService).runs(userId);
    }

    private EvaluationRunResponse response(UUID id) {
        return new EvaluationRunResponse(id, "dataset", "BM25", 10, EvaluationRunStatus.COMPLETED, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 10, 20, 100, Instant.now(), Instant.now());
    }
}
