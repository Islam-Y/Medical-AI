package com.medic.document.service;

import com.medic.document.dto.AnalysisResult;
import com.medic.document.dto.DocumentResponse;
import com.medic.document.entity.DocumentEntity;
import com.medic.document.entity.DocumentStatus;
import com.medic.document.repository.DocumentRepository;
import com.medic.events.document.ExtractedObservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DocumentServiceTest {

    private final DocumentRepository documentRepository = mock(DocumentRepository.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private final AnalysisClient analysisClient = mock(AnalysisClient.class);
    private DocumentService service;

    @TempDir
    private Path tempDir;

    @BeforeEach
    void setUp() {
        service = new DocumentService(documentRepository, outboxService, analysisClient);
        ReflectionTestUtils.setField(service, "storageRoot", tempDir);
    }

    @Test
    void uploadStoresFileRunsAnalysisAndPublishesEvents() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("file", "labs.pdf", "application/pdf", "content".getBytes());
        ExtractedObservation observation = new ExtractedObservation("Ferritin", BigDecimal.TEN, "ng/mL", "30-300", Instant.now());
        when(documentRepository.save(any(DocumentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(analysisClient.analyze(any(), any(), any())).thenReturn(new AnalysisResult(List.of(observation)));

        // Act
        DocumentResponse response = service.upload(userId, file);

        // Assert
        assertThat(response.status()).isEqualTo(DocumentStatus.EXTRACTED);
        assertThat(Files.list(tempDir.resolve(userId.toString())).count()).isEqualTo(1);
        verify(outboxService, times(2)).enqueue(any(), any(), any(), any());
        verify(analysisClient).analyze(any(), any(), any());
    }

    @Test
    void uploadRejectsEmptyFile() {
        // Arrange
        MockMultipartFile file = new MockMultipartFile("file", "empty.pdf", "application/pdf", new byte[0]);

        // Act
        // Assert
        assertThatThrownBy(() -> service.upload(UUID.randomUUID(), file))
                .isInstanceOf(StorageException.class);
    }

    @Test
    void documentReturnsDocumentOwnedByUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        DocumentEntity document = new DocumentEntity(userId, "labs.pdf", "application/pdf", 10, "/tmp/labs.pdf");
        when(documentRepository.findByIdAndUserId(document.getId(), userId)).thenReturn(Optional.of(document));

        // Act
        DocumentResponse response = service.document(userId, document.getId());

        // Assert
        assertThat(response.id()).isEqualTo(document.getId());
        assertThat(response.originalFileName()).isEqualTo("labs.pdf");
    }

    @Test
    void documentsReturnsUserDocuments() {
        // Arrange
        UUID userId = UUID.randomUUID();
        DocumentEntity document = new DocumentEntity(userId, "labs.pdf", "application/pdf", 10, "/tmp/labs.pdf");
        when(documentRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(document));

        // Act
        List<DocumentResponse> response = service.documents(userId);

        // Assert
        assertThat(response).extracting(DocumentResponse::originalFileName).containsExactly("labs.pdf");
    }

    @Test
    void documentRejectsMissingDocument() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        when(documentRepository.findByIdAndUserId(documentId, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.document(userId, documentId))
                .isInstanceOf(DocumentNotFoundException.class);
    }
}
