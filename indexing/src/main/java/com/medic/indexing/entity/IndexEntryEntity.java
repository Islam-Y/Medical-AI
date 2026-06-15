package com.medic.indexing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "index_entries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IndexEntryEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String sourceType;

    @Column(nullable = false)
    private UUID sourceId;

    @Column(nullable = false, unique = true)
    private UUID chunkId;

    private UUID documentId;

    private UUID extractionId;

    private String artifactBucket;

    @Column(columnDefinition = "text")
    private String artifactKey;

    private Integer pageNumber;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(nullable = false, columnDefinition = "text")
    private String sparseTerms;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public IndexEntryEntity(UUID userId, String sourceType, UUID sourceId, String title, String content, String sparseTerms) {
        this(userId, sourceType, sourceId, null, null, null, null, null, title, content, sparseTerms);
    }

    public IndexEntryEntity(
            UUID userId,
            String sourceType,
            UUID sourceId,
            UUID documentId,
            UUID extractionId,
            String artifactBucket,
            String artifactKey,
            Integer pageNumber,
            String title,
            String content,
            String sparseTerms
    ) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.chunkId = UUID.randomUUID();
        this.documentId = documentId;
        this.extractionId = extractionId;
        this.artifactBucket = artifactBucket;
        this.artifactKey = artifactKey;
        this.pageNumber = pageNumber;
        this.title = title;
        this.content = content;
        this.sparseTerms = sparseTerms;
    }

    public void update(String title, String content, String sparseTerms) {
        this.title = title;
        this.content = content;
        this.sparseTerms = sparseTerms;
    }

    public void updateProvenance(UUID documentId, UUID extractionId, String artifactBucket, String artifactKey, Integer pageNumber) {
        this.documentId = documentId;
        this.extractionId = extractionId;
        this.artifactBucket = artifactBucket;
        this.artifactKey = artifactKey;
        this.pageNumber = pageNumber;
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
