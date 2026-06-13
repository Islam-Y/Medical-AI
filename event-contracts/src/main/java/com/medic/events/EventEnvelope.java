package com.medic.events;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope<T>(
        UUID eventId,
        String eventType,
        int eventVersion,
        Instant occurredAt,
        UUID correlationId,
        UUID userId,
        T payload
) {
    public static <T> EventEnvelope<T> create(
            String eventType,
            UUID correlationId,
            UUID userId,
            T payload
    ) {
        return new EventEnvelope<>(
                UUID.randomUUID(),
                eventType,
                1,
                Instant.now(),
                correlationId,
                userId,
                payload
        );
    }
}
