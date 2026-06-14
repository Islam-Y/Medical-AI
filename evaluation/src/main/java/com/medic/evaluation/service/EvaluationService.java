package com.medic.evaluation.service;

import com.medic.evaluation.dto.BenchmarkMetrics;
import com.medic.evaluation.dto.CreateEvaluationRunRequest;
import com.medic.evaluation.dto.EvaluationRunResponse;
import com.medic.evaluation.entity.EvaluationRunEntity;
import com.medic.evaluation.repository.EvaluationRunRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final EvaluationRunRepository evaluationRunRepository;
    private final BenchmarkRunner benchmarkRunner;

    @Transactional
    public EvaluationRunResponse createRun(UUID userId, CreateEvaluationRunRequest request) {
        EvaluationRunEntity run = evaluationRunRepository.save(new EvaluationRunEntity(userId, request.datasetName(), request.algorithm(), request.queryCount()));
        BenchmarkMetrics metrics = benchmarkRunner.run(userId, request);
        run.complete(metrics);
        return toResponse(evaluationRunRepository.save(run));
    }

    @Transactional(readOnly = true)
    public List<EvaluationRunResponse> runs(UUID userId) {
        return evaluationRunRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EvaluationRunResponse run(UUID userId, UUID id) {
        return evaluationRunRepository.findByIdAndUserId(id, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new EvaluationRunNotFoundException(id));
    }

    private EvaluationRunResponse toResponse(EvaluationRunEntity run) {
        return new EvaluationRunResponse(
                run.getId(),
                run.getDatasetName(),
                run.getAlgorithm(),
                run.getQueryCount(),
                run.getStatus(),
                run.getRecallAtK(),
                run.getNdcgAtK(),
                run.getMrr(),
                run.getLatencyP95Ms(),
                run.getThroughputQps(),
                run.getIndexSizeBytes(),
                run.getCreatedAt(),
                run.getCompletedAt()
        );
    }
}
