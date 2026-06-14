package com.medic.chat.service;

import com.medic.chat.entity.ChatMessageEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class StubMedicalChatClient implements MedicalChatClient {

    @Override
    public String reply(UUID userId, UUID sessionId, String userMessage, List<ChatMessageEntity> context) {
        return "Medical chat inference is connected as a stub. The production adapter will use stored records, documents, and retrieval context.";
    }
}
