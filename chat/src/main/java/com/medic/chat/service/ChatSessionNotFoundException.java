package com.medic.chat.service;

import java.util.UUID;

public class ChatSessionNotFoundException extends RuntimeException {

    public ChatSessionNotFoundException(UUID sessionId) {
        super("Chat session not found: " + sessionId);
    }
}
