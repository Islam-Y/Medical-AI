package com.medic.healthrecord.repository;

import com.medic.healthrecord.entity.DiagnosisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DiagnosisRepository extends JpaRepository<DiagnosisEntity, UUID> {

    List<DiagnosisEntity> findByUserIdOrderByDiagnosedAtDesc(UUID userId);
}
