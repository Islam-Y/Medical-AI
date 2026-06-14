package com.medic.chat.dto;

import com.medic.chat.entity.ChatMessageRole;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
        UUID id,
        UUID sessionId,
        ChatMessageRole role,
        String content,
        Instant createdAt
) {
}
