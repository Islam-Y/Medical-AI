package com.medic.audit.repository;

import com.medic.audit.entity.AuditEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditEventRepository extends JpaRepository<AuditEventEntity, UUID> {

    List<AuditEventEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
