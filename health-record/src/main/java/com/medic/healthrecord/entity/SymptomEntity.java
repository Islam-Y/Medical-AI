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
@Table(name = "symptoms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SymptomEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int intensity;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false)
    private Instant observedAt;

    @Column(nullable = false)
    private Instant createdAt;

    public SymptomEntity(UUID userId, String name, int intensity, String notes, Instant observedAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.name = name;
        this.intensity = intensity;
        this.notes = notes;
        this.observedAt = observedAt;
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
