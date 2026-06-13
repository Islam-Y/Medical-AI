package com.medic.chat.service;

import com.medic.chat.entity.ChatMessageEntity;

import java.util.List;
import java.util.UUID;

public interface MedicalChatClient {

    String reply(UUID userId, UUID sessionId, String userMessage, List<ChatMessageEntity> context);
}
