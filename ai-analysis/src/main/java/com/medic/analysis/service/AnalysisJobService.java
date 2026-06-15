package com.medic.analysis.service;

import com.medic.analysis.config.ObjectStorageProperties;
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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnalysisJobService {

    private final AnalysisJobRepository analysisJobRepository;
    private final AnalysisClient analysisClient;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;
    private final ObjectStorageClient objectStorageClient;
    private final ObjectStorageProperties objectStorageProperties;

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
                root.path("payload").path("storageBucket").asString(),
                root.path("payload").path("storageKey").asString()
        ));
        AnalysisResult result = analyzeOriginal(job);
        StoredObject markdownArtifact = storeArtifact(job, "content.md", "text/markdown", result.canonicalMarkdown());
        StoredObject layoutArtifact = storeArtifact(job, "layout.json", "application/json", result.layoutJson());
        job.markCompleted(
                markdownArtifact.bucket(),
                markdownArtifact.key(),
                layoutArtifact.bucket(),
                layoutArtifact.key(),
                result.modelName(),
                result.modelVersion()
        );
        AnalysisJobEntity completed = analysisJobRepository.save(job);
        enqueueExtractionCompleted(completed, result);
    }

    private AnalysisResult analyzeOriginal(AnalysisJobEntity job) {
        try (StoredObjectContent originalObject = objectStorageClient.get(job.getStorageBucket(), job.getStorageKey())) {
            return analysisClient.analyze(new AnalysisInput(
                    job.getDocumentId(),
                    job.getFileName(),
                    job.getContentType(),
                    job.getStorageBucket(),
                    job.getStorageKey(),
                    originalObject
            ));
        } catch (Exception exception) {
            job.markFailed();
            analysisJobRepository.save(job);
            throw new IllegalStateException("Unable to analyze uploaded document", exception);
        }
    }

    private StoredObject storeArtifact(AnalysisJobEntity job, String fileName, String contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String key = "documents/%s/%s/extractions/%s/%s".formatted(
                job.getUserId(),
                job.getDocumentId(),
                job.getId(),
                fileName
        );
        return objectStorageClient.put(
                objectStorageProperties.getBuckets().getExtractedArtifacts(),
                key,
                contentType,
                new ByteArrayInputStream(bytes),
                bytes.length
        );
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
                        new DocumentExtractionCompletedEvent(
                                job.getDocumentId(),
                                job.getId(),
                                job.getStatus().name(),
                                job.getArtifactBucket(),
                                job.getArtifactKey(),
                                "text/markdown",
                                result.modelName(),
                                result.modelVersion(),
                                result.observations()
                        )
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
                job.getModelName(),
                job.getModelVersion(),
                artifactUri(job),
                job.getCreatedAt(),
                job.getCompletedAt()
        );
    }

    private String artifactUri(AnalysisJobEntity job) {
        if (job.getArtifactBucket() == null || job.getArtifactKey() == null) {
            return null;
        }
        return "s3://" + job.getArtifactBucket() + "/" + job.getArtifactKey();
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }
}
