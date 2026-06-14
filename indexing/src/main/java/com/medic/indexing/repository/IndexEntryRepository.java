package com.medic.indexing.repository;

import com.medic.indexing.entity.IndexEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IndexEntryRepository extends JpaRepository<IndexEntryEntity, UUID> {

    List<IndexEntryEntity> findByUserIdOrderByUpdatedAtDesc(UUID userId);

    Optional<IndexEntryEntity> findByIdAndUserId(UUID id, UUID userId);

    Optional<IndexEntryEntity> findByUserIdAndSourceTypeAndSourceId(UUID userId, String sourceType, UUID sourceId);
}
