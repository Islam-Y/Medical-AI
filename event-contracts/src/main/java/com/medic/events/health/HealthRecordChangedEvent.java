package com.medic.events.health;

import java.util.UUID;

public record HealthRecordChangedEvent(
        UUID recordId,
        String recordType,
        String changeType
) {
}
