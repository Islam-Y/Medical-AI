package com.medic.indexing.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.document.DocumentExtractionCompletedEvent;
import com.medic.indexing.dto.IndexEntryResponse;
import com.medic.indexing.dto.UpsertIndexEntryRequest;
import com.medic.indexing.entity.IndexEntryEntity;
import com.medic.indexing.repository.IndexEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IndexingServiceTest {

    private final IndexEntryRepository repository = mock(IndexEntryRepository.class);
    private final IndexBuilder indexBuilder = new StubIndexBuilder();
    private IndexingService service;

    @BeforeEach
    void setUp() {
        service = new IndexingService(repository, indexBuilder, new ObjectMapper());
    }

    @Test
    void upsertCreatesEntryWithSparseTerms() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpsertIndexEntryRequest request = new UpsertIndexEntryRequest("OBSERVATION", UUID.randomUUID(), "Ferritin", "Ferritin ferritin 10");
        when(repository.findByUserIdAndSourceTypeAndSourceId(userId, request.sourceType(), request.sourceId())).thenReturn(Optional.empty());
        when(repository.save(any(IndexEntryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        IndexEntryResponse response = service.upsert(userId, request);

        // Assert
        assertThat(response.sparseTerms()).isEqualTo("10 ferritin");
        verify(repository).save(any(IndexEntryEntity.class));
    }

    @Test
    void upsertUpdatesExistingEntry() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sourceId = UUID.randomUUID();
        IndexEntryEntity existing = new IndexEntryEntity(userId, "OBSERVATION", sourceId, "Old", "old", "old");
        UpsertIndexEntryRequest request = new UpsertIndexEntryRequest("OBSERVATION", sourceId, "New", "Vitamin D low");
        when(repository.findByUserIdAndSourceTypeAndSourceId(userId, request.sourceType(), request.sourceId())).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);

        // Act
        IndexEntryResponse response = service.upsert(userId, request);

        // Assert
        assertThat(response.title()).isEqualTo("New");
        assertThat(response.sparseTerms()).isEqualTo("d low vitamin");
    }

    @Test
    void entriesMapStoredRows() {
        // Arrange
        UUID userId = UUID.randomUUID();
        IndexEntryEntity entry = new IndexEntryEntity(userId, "DOC", UUID.randomUUID(), "Labs", "Ferritin 10", "10 ferritin");
        when(repository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(entry));

        // Act
        List<IndexEntryResponse> response = service.entries(userId);

        // Assert
        assertThat(response).extracting(IndexEntryResponse::title).containsExactly("Labs");
    }

    @Test
    void entryReturnsStoredEntry() {
        // Arrange
        UUID userId = UUID.randomUUID();
        IndexEntryEntity entry = new IndexEntryEntity(userId, "DOC", UUID.randomUUID(), "Labs", "Ferritin 10", "10 ferritin");
        when(repository.findByIdAndUserId(entry.getId(), userId)).thenReturn(Optional.of(entry));

        // Act
        IndexEntryResponse response = service.entry(userId, entry.getId());

        // Assert
        assertThat(response.id()).isEqualTo(entry.getId());
    }

    @Test
    void entryRejectsMissingEntry() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndUserId(id, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.entry(userId, id))
                .isInstanceOf(IndexEntryNotFoundException.class);
    }

    @Test
    void handleSourceEventUpsertsPayload() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.DOCUMENT_EXTRACTION_COMPLETED,
                UUID.randomUUID(),
                userId,
                new DocumentExtractionCompletedEvent(documentId, "COMPLETED", List.of())
        ));
        when(repository.findByUserIdAndSourceTypeAndSourceId(any(), any(), any())).thenReturn(Optional.empty());
        when(repository.save(any(IndexEntryEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.handleSourceEvent(message);

        // Assert
        verify(repository).save(any(IndexEntryEntity.class));
    }
}
