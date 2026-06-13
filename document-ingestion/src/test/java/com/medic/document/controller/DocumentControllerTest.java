package com.medic.document.controller;

import com.medic.document.dto.DocumentResponse;
import com.medic.document.entity.DocumentStatus;
import com.medic.document.service.DocumentService;
import com.medic.document.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentControllerTest {

    private final DocumentService documentService = mock(DocumentService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final DocumentController controller = new DocumentController(documentService, jwtService);

    @Test
    void uploadUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "labs.pdf", "application/pdf", "content".getBytes());
        DocumentResponse expected = new DocumentResponse(UUID.randomUUID(), "labs.pdf", "application/pdf", 7, DocumentStatus.EXTRACTED, Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(documentService.upload(userId, file)).thenReturn(expected);

        // Act
        DocumentResponse response = controller.upload("Bearer token", file);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(documentService).upload(userId, file);
    }

    @Test
    void listUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        DocumentResponse document = new DocumentResponse(UUID.randomUUID(), "labs.pdf", "application/pdf", 7, DocumentStatus.EXTRACTED, Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(documentService.documents(userId)).thenReturn(List.of(document));

        // Act
        List<DocumentResponse> response = controller.documents("Bearer token");

        // Assert
        assertThat(response).containsExactly(document);
        verify(documentService).documents(userId);
    }

    @Test
    void getUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        DocumentResponse document = new DocumentResponse(documentId, "labs.pdf", "application/pdf", 7, DocumentStatus.EXTRACTED, Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(documentService.document(userId, documentId)).thenReturn(document);

        // Act
        DocumentResponse response = controller.document("Bearer token", documentId);

        // Assert
        assertThat(response).isEqualTo(document);
        verify(documentService).document(userId, documentId);
    }
}
