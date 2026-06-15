package com.medic.analysis.entity;

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
@Table(name = "analysis_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnalysisJobEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID documentId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @Column(nullable = false)
    private String storageBucket;

    @Column(nullable = false, columnDefinition = "text")
    private String storageKey;

    private String artifactBucket;

    @Column(columnDefinition = "text")
    private String artifactKey;

    private String layoutArtifactBucket;

    @Column(columnDefinition = "text")
    private String layoutArtifactKey;

    private String modelName;

    private String modelVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisJobStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant completedAt;

    public AnalysisJobEntity(UUID documentId, UUID userId, String fileName, String contentType, String storageBucket, String storageKey) {
        this.id = UUID.randomUUID();
        this.documentId = documentId;
        this.userId = userId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.storageBucket = storageBucket;
        this.storageKey = storageKey;
        this.status = AnalysisJobStatus.PENDING;
    }

    public void markCompleted(
            String artifactBucket,
            String artifactKey,
            String layoutArtifactBucket,
            String layoutArtifactKey,
            String modelName,
            String modelVersion
    ) {
        this.status = AnalysisJobStatus.COMPLETED;
        this.artifactBucket = artifactBucket;
        this.artifactKey = artifactKey;
        this.layoutArtifactBucket = layoutArtifactBucket;
        this.layoutArtifactKey = layoutArtifactKey;
        this.modelName = modelName;
        this.modelVersion = modelVersion;
        this.completedAt = Instant.now();
    }

    public void markFailed() {
        this.status = AnalysisJobStatus.FAILED;
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
