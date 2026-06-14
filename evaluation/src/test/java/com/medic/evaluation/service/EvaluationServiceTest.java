package com.medic.evaluation.service;

import com.medic.evaluation.dto.BenchmarkMetrics;
import com.medic.evaluation.dto.CreateEvaluationRunRequest;
import com.medic.evaluation.entity.EvaluationRunEntity;
import com.medic.evaluation.entity.EvaluationRunStatus;
import com.medic.evaluation.repository.EvaluationRunRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EvaluationServiceTest {

    @Test
    void createRunStoresCompletedMetrics() {
        // Arrange
        EvaluationRunRepository repository = mock(EvaluationRunRepository.class);
        BenchmarkRunner runner = mock(BenchmarkRunner.class);
        EvaluationService service = new EvaluationService(repository, runner);
        UUID userId = UUID.randomUUID();
        CreateEvaluationRunRequest request = new CreateEvaluationRunRequest("dataset", "HYBRID", 50);
        BenchmarkMetrics metrics = new BenchmarkMetrics(BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, 10, 20, 30);
        when(repository.save(any(EvaluationRunEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(runner.run(userId, request)).thenReturn(metrics);

        // Act
        var response = service.createRun(userId, request);

        // Assert
        assertThat(response.status()).isEqualTo(EvaluationRunStatus.COMPLETED);
        assertThat(response.ndcgAtK()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void runsMapStoredRows() {
        // Arrange
        EvaluationRunRepository repository = mock(EvaluationRunRepository.class);
        EvaluationService service = new EvaluationService(repository, new StubBenchmarkRunner());
        UUID userId = UUID.randomUUID();
        EvaluationRunEntity run = new EvaluationRunEntity(userId, "dataset", "BM25", 10);
        when(repository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(run));

        // Act
        var response = service.runs(userId);

        // Assert
        assertThat(response).extracting("algorithm").containsExactly("BM25");
    }

    @Test
    void runRejectsMissingRun() {
        // Arrange
        EvaluationRunRepository repository = mock(EvaluationRunRepository.class);
        EvaluationService service = new EvaluationService(repository, new StubBenchmarkRunner());
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.run(userId, id))
                .isInstanceOf(EvaluationRunNotFoundException.class);
    }
}
