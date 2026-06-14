package com.medic.evaluation.service;

import com.medic.evaluation.dto.BenchmarkMetrics;
import com.medic.evaluation.dto.CreateEvaluationRunRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class StubBenchmarkRunner implements BenchmarkRunner {

    @Override
    public BenchmarkMetrics run(UUID userId, CreateEvaluationRunRequest request) {
        return new BenchmarkMetrics(
                BigDecimal.valueOf(0.75),
                BigDecimal.valueOf(0.70),
                BigDecimal.valueOf(0.68),
                120,
                250,
                1024
        );
    }
}
