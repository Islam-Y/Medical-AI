package com.medic.audit.controller;

import com.medic.audit.dto.AuditEventResponse;
import com.medic.audit.dto.RecordAuditEventRequest;
import com.medic.audit.service.AuditService;
import com.medic.audit.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditControllerTest {

    private final AuditService auditService = mock(AuditService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuditController controller = new AuditController(auditService, jwtService);

    @Test
    void recordUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        RecordAuditEventRequest request = new RecordAuditEventRequest("DOCUMENT_VIEWED", "DOCUMENT", "doc-1", "{}");
        AuditEventResponse expected = new AuditEventResponse(UUID.randomUUID(), request.action(), request.resourceType(), request.resourceId(), request.metadata(), Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(auditService.record(userId, request)).thenReturn(expected);

        // Act
        AuditEventResponse response = controller.record("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(auditService).record(userId, request);
    }

    @Test
    void eventsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        AuditEventResponse expected = new AuditEventResponse(UUID.randomUUID(), "LOGIN", "AUTH", null, null, Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(auditService.events(userId)).thenReturn(List.of(expected));

        // Act
        List<AuditEventResponse> response = controller.events("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(auditService).events(userId);
    }
}
