package com.medic.indexing.service;

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

    @Transactional
    public IndexEntryResponse upsert(UUID userId, UpsertIndexEntryRequest request) {
        String sparseTerms = indexBuilder.buildSparseTerms(request.content());
        IndexEntryEntity entry = indexEntryRepository.findByUserIdAndSourceTypeAndSourceId(userId, request.sourceType(), request.sourceId())
                .map(existing -> {
                    existing.update(request.title(), request.content(), sparseTerms);
                    return existing;
                })
                .orElseGet(() -> new IndexEntryEntity(userId, request.sourceType(), request.sourceId(), request.title(), request.content(), sparseTerms));
        return toResponse(indexEntryRepository.save(entry));
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
        UUID sourceId = UUID.fromString(root.path("payload").path("documentId").asString(root.path("payload").path("messageId").asString(root.path("eventId").asString())));
        upsert(userId, new UpsertIndexEntryRequest(eventType, sourceId, eventType, root.path("payload").toString()));
    }

    private IndexEntryResponse toResponse(IndexEntryEntity entry) {
        return new IndexEntryResponse(entry.getId(), entry.getSourceType(), entry.getSourceId(), entry.getTitle(), entry.getSparseTerms(), entry.getUpdatedAt());
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }
}
