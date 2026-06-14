package com.medic.events.chat;

import java.util.UUID;

public record ChatMessageCreatedEvent(
        UUID sessionId,
        UUID messageId,
        String role
) {
}
