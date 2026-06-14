package com.medic.audit.service;

import com.medic.audit.dto.RecordAuditEventRequest;
import com.medic.audit.entity.AuditEventEntity;
import com.medic.audit.repository.AuditEventRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditServiceTest {

    @Test
    void recordStoresAuditEvent() {
        // Arrange
        AuditEventRepository repository = mock(AuditEventRepository.class);
        AuditService service = new AuditService(repository);
        UUID userId = UUID.randomUUID();
        RecordAuditEventRequest request = new RecordAuditEventRequest("DOCUMENT_VIEWED", "DOCUMENT", "doc-1", "{}");
        when(repository.save(any(AuditEventEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = service.record(userId, request);

        // Assert
        assertThat(response.action()).isEqualTo("DOCUMENT_VIEWED");
        verify(repository).save(any(AuditEventEntity.class));
    }

    @Test
    void eventsMapStoredRows() {
        // Arrange
        AuditEventRepository repository = mock(AuditEventRepository.class);
        AuditService service = new AuditService(repository);
        UUID userId = UUID.randomUUID();
        AuditEventEntity event = new AuditEventEntity(userId, "LOGIN", "AUTH", null, null);
        when(repository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(event));

        // Act
        var response = service.events(userId);

        // Assert
        assertThat(response).extracting("action").containsExactly("LOGIN");
    }
}
