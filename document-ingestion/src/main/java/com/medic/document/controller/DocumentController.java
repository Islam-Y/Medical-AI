package com.medic.document.controller;

import com.medic.document.dto.DocumentResponse;
import com.medic.document.service.DocumentService;
import com.medic.document.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final JwtService jwtService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse upload(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("file") MultipartFile file
    ) {
        return documentService.upload(jwtService.parseBearerUserId(authorization), file);
    }

    @GetMapping
    public List<DocumentResponse> documents(@RequestHeader("Authorization") String authorization) {
        return documentService.documents(jwtService.parseBearerUserId(authorization));
    }

    @GetMapping("/{id}")
    public DocumentResponse document(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID id
    ) {
        return documentService.document(jwtService.parseBearerUserId(authorization), id);
    }
}
