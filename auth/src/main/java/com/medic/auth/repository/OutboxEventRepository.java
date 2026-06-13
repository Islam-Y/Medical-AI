package com.medic.auth.repository;

import com.medic.auth.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop20ByPublishedAtIsNullOrderByCreatedAtAsc();
}
