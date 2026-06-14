package com.medic.consent.controller;

import com.medic.consent.dto.ConsentResponse;
import com.medic.consent.dto.GrantConsentRequest;
import com.medic.consent.service.ConsentService;
import com.medic.consent.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final ConsentService consentService;
    private final JwtService jwtService;

    @PostMapping
    public ConsentResponse grant(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody GrantConsentRequest request
    ) {
        return consentService.grant(jwtService.parseBearerUserId(authorization), request);
    }

    @GetMapping
    public List<ConsentResponse> consents(@RequestHeader("Authorization") String authorization) {
        return consentService.consents(jwtService.parseBearerUserId(authorization));
    }

    @PatchMapping("/{id}/revoke")
    public ConsentResponse revoke(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return consentService.revoke(jwtService.parseBearerUserId(authorization), id);
    }
}
