package com.medic.healthrecord.repository;

import com.medic.healthrecord.entity.SymptomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SymptomRepository extends JpaRepository<SymptomEntity, UUID> {

    List<SymptomEntity> findByUserIdOrderByObservedAtDesc(UUID userId);
}
