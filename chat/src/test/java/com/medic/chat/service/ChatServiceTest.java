package com.medic.chat.service;

import com.medic.chat.dto.ChatMessageResponse;
import com.medic.chat.dto.ChatSessionResponse;
import com.medic.chat.dto.CreateChatSessionRequest;
import com.medic.chat.dto.SendChatMessageRequest;
import com.medic.chat.entity.ChatMessageEntity;
import com.medic.chat.entity.ChatMessageRole;
import com.medic.chat.entity.ChatSessionEntity;
import com.medic.chat.repository.ChatMessageRepository;
import com.medic.chat.repository.ChatSessionRepository;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private final ChatSessionRepository chatSessionRepository = mock(ChatSessionRepository.class);
    private final ChatMessageRepository chatMessageRepository = mock(ChatMessageRepository.class);
    private final MedicalChatClient medicalChatClient = mock(MedicalChatClient.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private ChatService service;

    @BeforeEach
    void setUp() {
        service = new ChatService(chatSessionRepository, chatMessageRepository, medicalChatClient, outboxService);
    }

    @Test
    void createSessionUsesDefaultTitleWhenBlank() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(chatSessionRepository.save(any(ChatSessionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ChatSessionResponse response = service.createSession(userId, new CreateChatSessionRequest(" "));

        // Assert
        assertThat(response.title()).isEqualTo("Medical chat");
        verify(chatSessionRepository).save(any(ChatSessionEntity.class));
    }

    @Test
    void sessionsMapStoredSessions() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChatSessionEntity session = new ChatSessionEntity(userId, "Labs");
        when(chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId)).thenReturn(List.of(session));

        // Act
        List<ChatSessionResponse> response = service.sessions(userId);

        // Assert
        assertThat(response).extracting(ChatSessionResponse::title).containsExactly("Labs");
    }

    @Test
    void messagesRequireSessionOwnership() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        ChatMessageEntity message = new ChatMessageEntity(sessionId, userId, ChatMessageRole.USER, "Hello");
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.of(new ChatSessionEntity(userId, "Labs")));
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId)).thenReturn(List.of(message));

        // Act
        List<ChatMessageResponse> response = service.messages(userId, sessionId);

        // Assert
        assertThat(response).extracting(ChatMessageResponse::content).containsExactly("Hello");
    }

    @Test
    void sendMessageStoresUserAndAssistantMessages() {
        // Arrange
        UUID userId = UUID.randomUUID();
        ChatSessionEntity session = new ChatSessionEntity(userId, "Labs");
        SendChatMessageRequest request = new SendChatMessageRequest("What changed?");
        when(chatSessionRepository.findByIdAndUserId(session.getId(), userId)).thenReturn(Optional.of(session));
        when(chatMessageRepository.save(any(ChatMessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId())).thenReturn(List.of());
        when(medicalChatClient.reply(eq(userId), eq(session.getId()), eq("What changed?"), any())).thenReturn("reply");

        // Act
        ChatMessageResponse response = service.sendMessage(userId, session.getId(), request);

        // Assert
        assertThat(response.role()).isEqualTo(ChatMessageRole.ASSISTANT);
        assertThat(response.content()).isEqualTo("reply");
        verify(chatMessageRepository, times(2)).save(any(ChatMessageEntity.class));
        verify(outboxService, times(2)).enqueue(eq(TopicNames.CHAT_EVENTS), eq(EventTypes.CHAT_MESSAGE_CREATED), any(), any());
        verify(chatSessionRepository).save(session);
    }

    @Test
    void sendMessageRejectsMissingSession() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID sessionId = UUID.randomUUID();
        when(chatSessionRepository.findByIdAndUserId(sessionId, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.sendMessage(userId, sessionId, new SendChatMessageRequest("Hello")))
                .isInstanceOf(ChatSessionNotFoundException.class);
    }
}
