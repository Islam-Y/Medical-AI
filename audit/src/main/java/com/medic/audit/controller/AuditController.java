package com.medic.audit.controller;

import com.medic.audit.dto.AuditEventResponse;
import com.medic.audit.dto.RecordAuditEventRequest;
import com.medic.audit.service.AuditService;
import com.medic.audit.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit/events")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;
    private final JwtService jwtService;

    @PostMapping
    public AuditEventResponse record(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody RecordAuditEventRequest request
    ) {
        return auditService.record(jwtService.parseBearerUserId(authorization), request);
    }

    @GetMapping
    public List<AuditEventResponse> events(@RequestHeader("Authorization") String authorization) {
        return auditService.events(jwtService.parseBearerUserId(authorization));
    }
}
