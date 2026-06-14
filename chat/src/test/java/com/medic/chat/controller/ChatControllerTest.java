package com.medic.chat.controller;

import com.medic.chat.dto.ChatMessageResponse;
import com.medic.chat.dto.ChatSessionResponse;
import com.medic.chat.dto.CreateChatSessionRequest;
import com.medic.chat.dto.SendChatMessageRequest;
import com.medic.chat.entity.ChatMessageRole;
import com.medic.chat.service.ChatService;
import com.medic.chat.service.JwtService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatControllerTest {

    private final ChatService chatService = mock(ChatService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final ChatController controller = new ChatController(chatService, jwtService);

    @Test
    void createSessionUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateChatSessionRequest request = new CreateChatSessionRequest("Back pain");
        ChatSessionResponse expected = new ChatSessionResponse(UUID.randomUUID(), "Back pain", Instant.now(), Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(chatService.createSession(userId, request)).thenReturn(expected);

        // Act
        ChatSessionResponse response = controller.createSession("Bearer token", request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(chatService).createSession(userId, request);
    }

    @Test
    void sessionsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChatSessionResponse expected = new ChatSessionResponse(UUID.randomUUID(), "Medical chat", Instant.now(), Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(chatService.sessions(userId)).thenReturn(List.of(expected));

        // Act
        List<ChatSessionResponse> response = controller.sessions("Bearer token");

        // Assert
        assertThat(response).containsExactly(expected);
        verify(chatService).sessions(userId);
    }

    @Test
    void sendMessageUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        SendChatMessageRequest request = new SendChatMessageRequest("What changed in my labs?");
        ChatMessageResponse expected = new ChatMessageResponse(UUID.randomUUID(), sessionId, ChatMessageRole.ASSISTANT, "reply", Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(chatService.sendMessage(userId, sessionId, request)).thenReturn(expected);

        // Act
        ChatMessageResponse response = controller.sendMessage("Bearer token", sessionId, request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(chatService).sendMessage(userId, sessionId, request);
    }

    @Test
    void messagesUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        ChatMessageResponse expected = new ChatMessageResponse(UUID.randomUUID(), sessionId, ChatMessageRole.USER, "Hello", Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(chatService.messages(userId, sessionId)).thenReturn(List.of(expected));

        // Act
        List<ChatMessageResponse> response = controller.messages("Bearer token", sessionId);

        // Assert
        assertThat(response).containsExactly(expected);
        verify(chatService).messages(userId, sessionId);
    }
}
