package com.medic.analysis.controller;

import com.medic.analysis.dto.AnalysisJobResponse;
import com.medic.analysis.service.AnalysisJobService;
import com.medic.analysis.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analysis/jobs")
@RequiredArgsConstructor
public class AnalysisJobController {

    private final AnalysisJobService analysisJobService;
    private final JwtService jwtService;

    @GetMapping
    public List<AnalysisJobResponse> jobs(@RequestHeader("Authorization") String authorization) {
        return analysisJobService.jobs(jwtService.parseBearerUserId(authorization));
    }

    @GetMapping("/{id}")
    public AnalysisJobResponse job(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return analysisJobService.job(jwtService.parseBearerUserId(authorization), id);
    }
}
