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

    @Column(nullable = false, columnDefinition = "text")
    private String storagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisJobStatus status;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private Instant completedAt;

    public AnalysisJobEntity(UUID documentId, UUID userId, String fileName, String contentType, String storagePath) {
        this.id = UUID.randomUUID();
        this.documentId = documentId;
        this.userId = userId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.storagePath = storagePath;
        this.status = AnalysisJobStatus.PENDING;
    }

    public void markCompleted() {
        this.status = AnalysisJobStatus.COMPLETED;
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
