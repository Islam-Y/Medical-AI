package com.medic.consent.repository;

import com.medic.consent.entity.ConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsentRepository extends JpaRepository<ConsentEntity, UUID> {

    List<ConsentEntity> findByUserIdOrderByGrantedAtDesc(UUID userId);

    Optional<ConsentEntity> findByIdAndUserId(UUID id, UUID userId);
}
