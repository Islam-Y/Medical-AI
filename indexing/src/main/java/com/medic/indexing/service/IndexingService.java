package com.medic.indexing.service;

import com.medic.events.EventTypes;
import com.medic.indexing.dto.IndexEntryResponse;
import com.medic.indexing.dto.UpsertIndexEntryRequest;
import com.medic.indexing.entity.IndexEntryEntity;
import com.medic.indexing.repository.IndexEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndexingService {

    private final IndexEntryRepository indexEntryRepository;
    private final IndexBuilder indexBuilder;
    private final ObjectMapper objectMapper;
    private final ObjectStorageClient objectStorageClient;
    private final SearchIndexClient searchIndexClient;

    @Transactional
    public IndexEntryResponse upsert(UUID userId, UpsertIndexEntryRequest request) {
        String sparseTerms = indexBuilder.buildSparseTerms(request.content());
        IndexEntryEntity entry = indexEntryRepository.findByUserIdAndSourceTypeAndSourceId(userId, request.sourceType(), request.sourceId())
                .map(existing -> {
                    existing.update(request.title(), request.content(), sparseTerms);
                    existing.updateProvenance(
                            request.documentId(),
                            request.extractionId(),
                            request.artifactBucket(),
                            request.artifactKey(),
                            request.pageNumber()
                    );
                    return existing;
                })
                .orElseGet(() -> new IndexEntryEntity(
                        userId,
                        request.sourceType(),
                        request.sourceId(),
                        request.documentId(),
                        request.extractionId(),
                        request.artifactBucket(),
                        request.artifactKey(),
                        request.pageNumber(),
                        request.title(),
                        request.content(),
                        sparseTerms
                ));
        IndexEntryEntity saved = indexEntryRepository.save(entry);
        searchIndexClient.index(toIndexDocument(saved));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<IndexEntryResponse> entries(UUID userId) {
        return indexEntryRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public IndexEntryResponse entry(UUID userId, UUID id) {
        return indexEntryRepository.findByIdAndUserId(id, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new IndexEntryNotFoundException(id));
    }

    @Transactional
    public void handleSourceEvent(String message) {
        JsonNode root = readTree(message);
        String eventType = root.path("eventType").asString();
        UUID userId = UUID.fromString(root.path("userId").asString());
        JsonNode payload = root.path("payload");
        UUID sourceId = UUID.fromString(payload.path("documentId").asString(payload.path("messageId").asString(root.path("eventId").asString())));
        if (EventTypes.DOCUMENT_EXTRACTION_COMPLETED.equals(eventType) && !payload.path("artifactBucket").isMissingNode()) {
            upsert(userId, new UpsertIndexEntryRequest(
                    eventType,
                    sourceId,
                    sourceId,
                    UUID.fromString(payload.path("extractionId").asString()),
                    payload.path("artifactBucket").asString(),
                    payload.path("artifactKey").asString(),
                    1,
                    "Canonical extraction " + sourceId,
                    objectStorageClient.readText(payload.path("artifactBucket").asString(), payload.path("artifactKey").asString())
            ));
            return;
        }
        upsert(userId, new UpsertIndexEntryRequest(eventType, sourceId, null, null, null, null, null, eventType, payload.toString()));
    }

    private IndexEntryResponse toResponse(IndexEntryEntity entry) {
        return new IndexEntryResponse(
                entry.getId(),
                entry.getChunkId(),
                entry.getSourceType(),
                entry.getSourceId(),
                entry.getDocumentId(),
                entry.getExtractionId(),
                entry.getTitle(),
                entry.getSparseTerms(),
                artifactUri(entry),
                entry.getPageNumber(),
                entry.getUpdatedAt()
        );
    }

    private IndexDocument toIndexDocument(IndexEntryEntity entry) {
        return new IndexDocument(
                entry.getChunkId(),
                entry.getUserId(),
                entry.getSourceType(),
                entry.getSourceId(),
                entry.getDocumentId(),
                entry.getExtractionId(),
                entry.getTitle(),
                entry.getContent(),
                entry.getSparseTerms(),
                artifactUri(entry),
                entry.getPageNumber(),
                entry.getUpdatedAt()
        );
    }

    private String artifactUri(IndexEntryEntity entry) {
        if (entry.getArtifactBucket() == null || entry.getArtifactKey() == null) {
            return null;
        }
        return "s3://" + entry.getArtifactBucket() + "/" + entry.getArtifactKey();
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }
}
