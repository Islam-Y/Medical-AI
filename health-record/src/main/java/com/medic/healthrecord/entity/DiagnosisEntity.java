package com.medic.healthrecord.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "diagnoses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiagnosisEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Instant diagnosedAt;

    private String source;

    @Column(nullable = false)
    private Instant createdAt;

    public DiagnosisEntity(UUID userId, String name, Instant diagnosedAt, String source) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.diagnosedAt = diagnosedAt;
        this.source = source;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
