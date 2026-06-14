package com.medic.evaluation.service;

import com.medic.evaluation.dto.BenchmarkMetrics;
import com.medic.evaluation.dto.CreateEvaluationRunRequest;

import java.util.UUID;

public interface BenchmarkRunner {

    BenchmarkMetrics run(UUID userId, CreateEvaluationRunRequest request);
}
