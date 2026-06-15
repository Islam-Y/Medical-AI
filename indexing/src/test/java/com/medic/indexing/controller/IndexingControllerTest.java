package com.medic.indexing.controller;

import com.medic.indexing.dto.IndexEntryResponse;
import com.medic.indexing.dto.UpsertIndexEntryRequest;
import com.medic.indexing.service.IndexingService;
import com.medic.indexing.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndexingControllerTest {

    private final IndexingService indexingService = mock(IndexingService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final IndexingController controller = new IndexingController(indexingService, jwtService);

    @Test
    void upsertUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpsertIndexEntryRequest request = new UpsertIndexEntryRequest("OBSERVATION", UUID.randomUUID(), null, null, null, null, null, "Ferritin", "Ferritin 10");
        IndexEntryResponse expected = new IndexEntryResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                request.sourceType(),
                request.sourceId(),
                null,
                null,
                request.title(),
                "10 ferritin",
                null,
                null,
                Instant.now()
        );
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(indexingService.upsert(userId, request)).thenReturn(expected);

        // Act
        IndexEntryResponse response = controller.upsert("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(indexingService).upsert(userId, request);
    }

    @Test
    void entriesUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        IndexEntryResponse expected = new IndexEntryResponse(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "DOC",
                UUID.randomUUID(),
                null,
                null,
                "Doc",
                "doc",
                null,
                null,
                Instant.now()
        );
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(indexingService.entries(userId)).thenReturn(List.of(expected));

        // Act
        List<IndexEntryResponse> response = controller.entries("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(indexingService).entries(userId);
    }

    @Test
    void entryUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID entryId = UUID.randomUUID();
        IndexEntryResponse expected = new IndexEntryResponse(
                entryId,
                UUID.randomUUID(),
                "DOC",
                UUID.randomUUID(),
                null,
                null,
                "Doc",
                "doc",
                null,
                null,
                Instant.now()
        );
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(indexingService.entry(userId, entryId)).thenReturn(expected);

        // Act
        IndexEntryResponse response = controller.entry("Bearer token", entryId);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(indexingService).entry(userId, entryId);
    }
}
