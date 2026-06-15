package com.medic.document.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private String storageBucket;

    @Column(nullable = false, columnDefinition = "text")
    private String storageKey;

    @Column(nullable = false, length = 64)
    private String checksumSha256;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public DocumentEntity(
            UUID userId,
            String originalFileName,
            String contentType,
            long sizeBytes,
            String storageBucket,
            String storageKey,
            String checksumSha256
    ) {
        this(UUID.randomUUID(), userId, originalFileName, contentType, sizeBytes, storageBucket, storageKey, checksumSha256);
    }

    public DocumentEntity(
            UUID id,
            UUID userId,
            String originalFileName,
            String contentType,
            long sizeBytes,
            String storageBucket,
            String storageKey,
            String checksumSha256
    ) {
        this.id = id;
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.contentType = contentType;
        this.sizeBytes = sizeBytes;
        this.storageBucket = storageBucket;
        this.storageKey = storageKey;
        this.checksumSha256 = checksumSha256;
        this.status = DocumentStatus.UPLOADED;
    }

    public void markExtracted() {
        this.status = DocumentStatus.EXTRACTED;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
