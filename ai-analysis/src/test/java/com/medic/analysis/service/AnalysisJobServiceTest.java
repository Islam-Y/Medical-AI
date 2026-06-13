package com.medic.analysis.service;

import com.medic.analysis.dto.AnalysisResult;
import com.medic.analysis.dto.AnalysisJobResponse;
import com.medic.analysis.entity.AnalysisJobEntity;
import com.medic.analysis.entity.AnalysisJobStatus;
import com.medic.analysis.repository.AnalysisJobRepository;
import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.document.DocumentUploadedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AnalysisJobServiceTest {

    private final AnalysisJobRepository analysisJobRepository = mock(AnalysisJobRepository.class);
    private final AnalysisClient analysisClient = mock(AnalysisClient.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private AnalysisJobService service;

    @BeforeEach
    void setUp() {
        service = new AnalysisJobService(analysisJobRepository, analysisClient, outboxService, new ObjectMapper());
    }

    @Test
    void jobsMapsStoredJobs() {
        // Arrange
        UUID userId = UUID.randomUUID();
        AnalysisJobEntity job = new AnalysisJobEntity(UUID.randomUUID(), userId, "labs.pdf", "application/pdf", "/tmp/labs.pdf");
        when(analysisJobRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(job));

        // Act
        List<AnalysisJobResponse> response = service.jobs(userId);

        // Assert
        assertThat(response).extracting(AnalysisJobResponse::fileName).containsExactly("labs.pdf");
    }

    @Test
    void jobRejectsMissingJob() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID jobId = UUID.randomUUID();
        when(analysisJobRepository.findByIdAndUserId(jobId, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.job(userId, jobId))
                .isInstanceOf(AnalysisJobNotFoundException.class);
    }

    @Test
    void handleDocumentEventCreatesJobAndPublishesCompletedEvent() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.DOCUMENT_UPLOADED,
                UUID.randomUUID(),
                userId,
                new DocumentUploadedEvent(documentId, "labs.pdf", "application/pdf", "/tmp/labs.pdf")
        ));
        when(analysisJobRepository.existsByDocumentId(documentId)).thenReturn(false);
        when(analysisJobRepository.save(any(AnalysisJobEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(analysisClient.analyze(eq(documentId), eq(Path.of("/tmp/labs.pdf")), eq("application/pdf"))).thenReturn(AnalysisResult.empty());

        // Act
        service.handleDocumentEvent(message);

        // Assert
        verify(analysisClient).analyze(eq(documentId), eq(Path.of("/tmp/labs.pdf")), eq("application/pdf"));
        verify(outboxService).enqueue(eq(TopicNames.DOCUMENT_EVENTS), eq(EventTypes.DOCUMENT_EXTRACTION_COMPLETED), eq(documentId), any());
    }

    @Test
    void handleDocumentEventIgnoresDifferentEventType() throws Exception {
        // Arrange
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.DOCUMENT_EXTRACTION_COMPLETED,
                UUID.randomUUID(),
                UUID.randomUUID(),
                "payload"
        ));

        // Act
        service.handleDocumentEvent(message);

        // Assert
        verify(analysisJobRepository, never()).save(any());
        verify(analysisClient, never()).analyze(any(), any(), any());
    }

    @Test
    void handleDocumentEventIgnoresDuplicateDocument() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID documentId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.DOCUMENT_UPLOADED,
                UUID.randomUUID(),
                userId,
                new DocumentUploadedEvent(documentId, "labs.pdf", "application/pdf", "/tmp/labs.pdf")
        ));
        when(analysisJobRepository.existsByDocumentId(documentId)).thenReturn(true);

        // Act
        service.handleDocumentEvent(message);

        // Assert
        verify(analysisJobRepository, never()).save(any());
        verify(analysisClient, never()).analyze(any(), any(), any());
    }
}
