package com.medic.analysis.repository;

import com.medic.analysis.entity.AnalysisJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJobEntity, UUID> {

    boolean existsByDocumentId(UUID documentId);

    List<AnalysisJobEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<AnalysisJobEntity> findByIdAndUserId(UUID id, UUID userId);
}
