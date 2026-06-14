package com.medic.analysis.service;

import com.medic.analysis.dto.AnalysisJobResponse;
import com.medic.analysis.dto.AnalysisResult;
import com.medic.analysis.entity.AnalysisJobEntity;
import com.medic.analysis.repository.AnalysisJobRepository;
import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.document.DocumentExtractionCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisJobService {

    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisClient analysisClient;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<AnalysisJobResponse> jobs(UUID userId) {
        return analysisJobRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AnalysisJobResponse job(UUID userId, UUID jobId) {
        return analysisJobRepository.findByIdAndUserId(jobId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new AnalysisJobNotFoundException(jobId));
    }

    @Transactional
    public void handleDocumentEvent(String message) {
        JsonNode root = readTree(message);
        if (!EventTypes.DOCUMENT_UPLOADED.equals(root.path("eventType").asString())) {
            return;
        }
        UUID documentId = UUID.fromString(root.path("payload").path("documentId").asString());
        if (analysisJobRepository.existsByDocumentId(documentId)) {
            return;
        }
        AnalysisJobEntity job = analysisJobRepository.save(new AnalysisJobEntity(
                documentId,
                UUID.fromString(root.path("userId").asString()),
                root.path("payload").path("fileName").asString(),
                root.path("payload").path("contentType").asString(),
                root.path("payload").path("storagePath").asString()
        ));
        AnalysisResult result = analysisClient.analyze(job.getDocumentId(), Path.of(job.getStoragePath()), job.getContentType());
        job.markCompleted();
        AnalysisJobEntity completed = analysisJobRepository.save(job);
        enqueueExtractionCompleted(completed, result);
    }

    private void enqueueExtractionCompleted(AnalysisJobEntity job, AnalysisResult result) {
        outboxService.enqueue(
                TopicNames.DOCUMENT_EVENTS,
                EventTypes.DOCUMENT_EXTRACTION_COMPLETED,
                job.getDocumentId(),
                EventEnvelope.create(
                        EventTypes.DOCUMENT_EXTRACTION_COMPLETED,
                        UUID.randomUUID(),
                        job.getUserId(),
                        new DocumentExtractionCompletedEvent(job.getDocumentId(), job.getStatus().name(), result.observations())
                )
        );
    }

    private AnalysisJobResponse toResponse(AnalysisJobEntity job) {
        return new AnalysisJobResponse(
                job.getId(),
                job.getDocumentId(),
                job.getFileName(),
                job.getContentType(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getCompletedAt()
        );
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }
}
