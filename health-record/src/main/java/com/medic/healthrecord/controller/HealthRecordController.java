package com.medic.healthrecord.controller;

import com.medic.healthrecord.dto.CreateDiagnosisRequest;
import com.medic.healthrecord.dto.CreateObservationRequest;
import com.medic.healthrecord.dto.CreateSymptomRequest;
import com.medic.healthrecord.dto.DiagnosisResponse;
import com.medic.healthrecord.dto.ObservationResponse;
import com.medic.healthrecord.dto.SymptomResponse;
import com.medic.healthrecord.dto.TimelineItemResponse;
import com.medic.healthrecord.service.HealthRecordService;
import com.medic.healthrecord.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthRecordController {

    private final HealthRecordService healthRecordService;
    private final JwtService jwtService;

    @PostMapping("/observations")
    @ResponseStatus(HttpStatus.CREATED)
    public ObservationResponse createObservation(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateObservationRequest request
    ) {
        return healthRecordService.addObservation(userId(authorization), request);
    }

    @GetMapping("/observations")
    public List<ObservationResponse> observations(@RequestHeader("Authorization") String authorization) {
        return healthRecordService.observations(userId(authorization));
    }

    @PostMapping("/symptoms")
    @ResponseStatus(HttpStatus.CREATED)
    public SymptomResponse createSymptom(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateSymptomRequest request
    ) {
        return healthRecordService.addSymptom(userId(authorization), request);
    }

    @GetMapping("/symptoms")
    public List<SymptomResponse> symptoms(@RequestHeader("Authorization") String authorization) {
        return healthRecordService.symptoms(userId(authorization));
    }

    @PostMapping("/diagnoses")
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisResponse createDiagnosis(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CreateDiagnosisRequest request
    ) {
        return healthRecordService.addDiagnosis(userId(authorization), request);
    }

    @GetMapping("/diagnoses")
    public List<DiagnosisResponse> diagnoses(@RequestHeader("Authorization") String authorization) {
        return healthRecordService.diagnoses(userId(authorization));
    }

    @GetMapping("/timeline")
    public List<TimelineItemResponse> timeline(@RequestHeader("Authorization") String authorization) {
        return healthRecordService.timeline(userId(authorization));
    }

    private UUID userId(String authorization) {
        return jwtService.parseBearerUserId(authorization);
    }
}
