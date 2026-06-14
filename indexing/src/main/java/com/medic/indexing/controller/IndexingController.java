package com.medic.indexing.controller;

import com.medic.indexing.dto.IndexEntryResponse;
import com.medic.indexing.dto.UpsertIndexEntryRequest;
import com.medic.indexing.service.IndexingService;
import com.medic.indexing.service.JwtService;
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
@RequestMapping("/api/v1/index")
@RequiredArgsConstructor
public class IndexingController {

    private final IndexingService indexingService;
    private final JwtService jwtService;

    @PostMapping("/entries")
    public IndexEntryResponse upsert(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpsertIndexEntryRequest request
    ) {
        return indexingService.upsert(jwtService.parseBearerUserId(authorization), request);
    }

    @GetMapping("/entries")
    public List<IndexEntryResponse> entries(@RequestHeader("Authorization") String authorization) {
        return indexingService.entries(jwtService.parseBearerUserId(authorization));
    }

    @GetMapping("/entries/{id}")
    public IndexEntryResponse entry(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return indexingService.entry(jwtService.parseBearerUserId(authorization), id);
    }
}
