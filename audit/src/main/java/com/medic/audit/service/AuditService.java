package com.medic.audit.service;

import com.medic.audit.dto.AuditEventResponse;
import com.medic.audit.dto.RecordAuditEventRequest;
import com.medic.audit.entity.AuditEventEntity;
import com.medic.audit.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    @Transactional
    public AuditEventResponse record(UUID userId, RecordAuditEventRequest request) {
        AuditEventEntity event = auditEventRepository.save(new AuditEventEntity(
                userId,
                request.action(),
                request.resourceType(),
                request.resourceId(),
                request.metadata()
        ));
        return toResponse(event);
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> events(UUID userId) {
        return auditEventRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditEventResponse toResponse(AuditEventEntity event) {
        return new AuditEventResponse(event.getId(), event.getAction(), event.getResourceType(), event.getResourceId(), event.getMetadata(), event.getCreatedAt());
    }
}
