package com.medic.healthrecord.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "observations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObservationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    @Column(nullable = false)
    private String unit;

    private String referenceRange;

    @Column(nullable = false)
    private Instant observedAt;

    private UUID sourceDocumentId;

    @Column(nullable = false)
    private Instant createdAt;

    public ObservationEntity(
            UUID userId,
            String name,
            BigDecimal value,
            String unit,
            String referenceRange,
            Instant observedAt,
            UUID sourceDocumentId
    ) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.referenceRange = referenceRange;
        this.observedAt = observedAt;
        this.sourceDocumentId = sourceDocumentId;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
