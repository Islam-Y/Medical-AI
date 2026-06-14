package com.medic.evaluation.controller;

import com.medic.evaluation.dto.CreateEvaluationRunRequest;
import com.medic.evaluation.dto.EvaluationRunResponse;
import com.medic.evaluation.service.EvaluationService;
import com.medic.evaluation.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/evaluation")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final JwtService jwtService;

    @PostMapping("/runs")
    public EvaluationRunResponse createRun(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateEvaluationRunRequest request
    ) {
        return evaluationService.createRun(jwtService.parseBearerUserId(authorization), request);
    }

    @GetMapping("/runs")
    public List<EvaluationRunResponse> runs(@RequestHeader("Authorization") String authorization) {
        return evaluationService.runs(jwtService.parseBearerUserId(authorization));
    }

    @GetMapping("/runs/{id}")
    public EvaluationRunResponse run(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return evaluationService.run(jwtService.parseBearerUserId(authorization), id);
    }
}
