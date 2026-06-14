package com.medic.retrieval.entity;

import com.medic.retrieval.dto.RetrievalMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "retrieval_queries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RetrievalQueryEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, columnDefinition = "text")
    private String queryText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RetrievalMode mode;

    @Column(nullable = false)
    private int topK;

    @Column(nullable = false)
    private long latencyMs;

    @Column(nullable = false)
    private int resultCount;

    @Column(nullable = false)
    private Instant createdAt;

    public RetrievalQueryEntity(UUID userId, String queryText, RetrievalMode mode, int topK, long latencyMs, int resultCount) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.queryText = queryText;
        this.mode = mode;
        this.topK = topK;
        this.latencyMs = latencyMs;
        this.resultCount = resultCount;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
