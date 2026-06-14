package com.medic.consent.entity;

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
@Table(name = "consents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsentEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentScope scope;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsentStatus status;

    private String subject;

    @Column(nullable = false)
    private Instant grantedAt;

    private Instant revokedAt;

    public ConsentEntity(UUID userId, ConsentScope scope, String subject) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.scope = scope;
        this.subject = subject;
        this.status = ConsentStatus.ACTIVE;
    }

    public void revoke() {
        this.status = ConsentStatus.REVOKED;
        this.revokedAt = Instant.now();
    }

    @PrePersist
    void prePersist() {
        if (grantedAt == null) {
            grantedAt = Instant.now();
        }
    }
}
