package com.medic.document.service;

import com.medic.document.dto.DocumentResponse;
import com.medic.document.entity.DocumentEntity;
import com.medic.document.repository.DocumentRepository;
import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.document.DocumentUploadedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Value("${storage.documents.path}")
    private Path storageRoot;

    @Transactional
    public DocumentResponse upload(UUID userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Uploaded file is empty");
        }
        UUID documentId = UUID.randomUUID();
        Path target = writeFile(userId, documentId, file);
        DocumentEntity document = documentRepository.save(new DocumentEntity(
                userId,
                originalFileName(file),
                contentType(file),
                file.getSize(),
                target.toString()
        ));
        enqueueUploaded(document);
        return toResponse(document);
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> documents(UUID userId) {
        return documentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentResponse document(UUID userId, UUID documentId) {
        return documentRepository.findByIdAndUserId(documentId, userId)
                .map(this::toResponse)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    @Transactional
    public void handleDocumentEvent(String message) {
        JsonNode root = readTree(message);
        if (!EventTypes.DOCUMENT_EXTRACTION_COMPLETED.equals(root.path("eventType").asString())) {
            return;
        }
        UUID documentId = UUID.fromString(root.path("payload").path("documentId").asString());
        documentRepository.findById(documentId).ifPresent(document -> {
            document.markExtracted();
            documentRepository.save(document);
        });
    }

    private void enqueueUploaded(DocumentEntity document) {
        outboxService.enqueue(
                TopicNames.DOCUMENT_EVENTS,
                EventTypes.DOCUMENT_UPLOADED,
                document.getId(),
                EventEnvelope.create(
                        EventTypes.DOCUMENT_UPLOADED,
                        UUID.randomUUID(),
                        document.getUserId(),
                        new DocumentUploadedEvent(
                                document.getId(),
                                document.getOriginalFileName(),
                                document.getContentType(),
                                document.getStoragePath()
                        )
                )
        );
    }

    private Path writeFile(UUID userId, UUID documentId, MultipartFile file) {
        try {
            Path userDirectory = storageRoot.resolve(userId.toString()).normalize();
            Files.createDirectories(userDirectory);
            Path target = userDirectory.resolve(documentId + "-" + sanitize(originalFileName(file))).normalize();
            if (!target.startsWith(userDirectory)) {
                throw new StorageException("Invalid file name");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target);
            }
            return target;
        } catch (StorageException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new StorageException("Unable to store document", exception);
        }
    }

    private DocumentResponse toResponse(DocumentEntity document) {
        return new DocumentResponse(
                document.getId(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getSizeBytes(),
                document.getStatus(),
                document.getCreatedAt()
        );
    }

    private String originalFileName(MultipartFile file) {
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            return "document";
        }
        return original;
    }

    private String contentType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return "application/octet-stream";
        }
        return contentType;
    }

    private String sanitize(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }
}
