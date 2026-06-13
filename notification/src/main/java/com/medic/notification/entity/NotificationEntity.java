package com.medic.notification.entity;

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
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String message;

    @Column(nullable = false)
    private String type;

    @Column(name = "read_flag", nullable = false)
    private boolean read;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant readAt;

    public NotificationEntity(UUID userId, String title, String message, String type) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.read = false;
    }

    public void markRead() {
        if (!read) {
            read = true;
            readAt = Instant.now();
        }
    }

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
