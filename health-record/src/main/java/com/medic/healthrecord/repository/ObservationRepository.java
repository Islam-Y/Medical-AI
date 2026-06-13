package com.medic.healthrecord.repository;

import com.medic.healthrecord.entity.ObservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ObservationRepository extends JpaRepository<ObservationEntity, UUID> {

    List<ObservationEntity> findByUserIdOrderByObservedAtDesc(UUID userId);
}
