package com.medic.retrieval.controller;

import com.medic.retrieval.dto.RetrievalMode;
import com.medic.retrieval.dto.RetrievalResponse;
import com.medic.retrieval.dto.RetrievalSearchRequest;
import com.medic.retrieval.service.JwtService;
import com.medic.retrieval.service.RetrievalService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RetrievalControllerTest {

    private final RetrievalService retrievalService = mock(RetrievalService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final RetrievalController controller = new RetrievalController(retrievalService, jwtService);

    @Test
    void searchUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        RetrievalSearchRequest request = new RetrievalSearchRequest("ferritin trend", RetrievalMode.HYBRID, 5);
        RetrievalResponse expected = new RetrievalResponse(UUID.randomUUID(), request.query(), request.mode(), 10, List.of());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(retrievalService.search(userId, request)).thenReturn(expected);

        // Act
        RetrievalResponse response = controller.search("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(retrievalService).search(userId, request);
    }
}
