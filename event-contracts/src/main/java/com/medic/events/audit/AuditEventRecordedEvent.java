package com.medic.events.audit;

import java.util.UUID;

public record AuditEventRecordedEvent(
        UUID auditEventId,
        String action,
        String resourceType
) {
}
