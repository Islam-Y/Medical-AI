package com.medic.evaluation.repository;

import com.medic.evaluation.entity.EvaluationRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvaluationRunRepository extends JpaRepository<EvaluationRunEntity, UUID> {

    List<EvaluationRunEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<EvaluationRunEntity> findByIdAndUserId(UUID id, UUID userId);
}
