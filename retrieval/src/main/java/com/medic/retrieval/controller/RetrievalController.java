package com.medic.retrieval.controller;

import com.medic.retrieval.dto.RetrievalResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import com.medic.retrieval.service.JwtService;
import com.medic.retrieval.service.RetrievalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/retrieval")
@RequiredArgsConstructor
public class RetrievalController {

    private final RetrievalService retrievalService;
    private final JwtService jwtService;

    @PostMapping("/search")
    public RetrievalResponse search(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody RetrievalSearchRequest request
    ) {
        return retrievalService.search(jwtService.parseBearerUserId(authorization), request);
    }
}
