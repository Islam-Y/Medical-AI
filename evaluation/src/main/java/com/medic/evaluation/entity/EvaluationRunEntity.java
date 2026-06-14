package com.medic.evaluation.entity;

import com.medic.evaluation.dto.BenchmarkMetrics;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "evaluation_runs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EvaluationRunEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String datasetName;

    @Column(nullable = false)
    private String algorithm;

    @Column(nullable = false)
    private int queryCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EvaluationRunStatus status;

    private BigDecimal recallAtK;

    private BigDecimal ndcgAtK;

    private BigDecimal mrr;

    private long latencyP95Ms;

    private long throughputQps;

    private long indexSizeBytes;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant completedAt;

    public EvaluationRunEntity(UUID userId, String datasetName, String algorithm, int queryCount) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.datasetName = datasetName;
        this.algorithm = algorithm;
        this.queryCount = queryCount;
        this.status = EvaluationRunStatus.RUNNING;
    }

    public void complete(BenchmarkMetrics metrics) {
        this.status = EvaluationRunStatus.COMPLETED;
        this.recallAtK = metrics.recallAtK();
        this.ndcgAtK = metrics.ndcgAtK();
        this.mrr = metrics.mrr();
        this.latencyP95Ms = metrics.latencyP95Ms();
        this.throughputQps = metrics.throughputQps();
        this.indexSizeBytes = metrics.indexSizeBytes();
        this.completedAt = Instant.now();
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
