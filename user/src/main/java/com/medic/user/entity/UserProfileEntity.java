package com.medic.user.entity;

import com.medic.user.dto.UpdateUserProfileRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfileEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private UUID userId;

    private String email;

    private BigDecimal heightCm;

    private BigDecimal weightKg;

    private LocalDate birthDate;

    private String sex;

    @Column(columnDefinition = "text")
    private String knownDiagnoses;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    public UserProfileEntity(UUID userId, String email) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.email = email;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void update(UpdateUserProfileRequest request) {
        this.heightCm = request.heightCm();
        this.weightKg = request.weightKg();
        this.birthDate = request.birthDate();
        this.sex = request.sex();
        this.knownDiagnoses = request.knownDiagnoses();
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
