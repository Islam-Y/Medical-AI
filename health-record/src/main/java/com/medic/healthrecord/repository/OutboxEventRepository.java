package com.medic.healthrecord.repository;

import com.medic.healthrecord.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop20ByPublishedAtIsNullOrderByCreatedAtAsc();
}
