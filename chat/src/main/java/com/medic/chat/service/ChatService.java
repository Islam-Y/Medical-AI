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
import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.chat.ChatMessageCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String DEFAULT_TITLE = "Medical chat";

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MedicalChatClient medicalChatClient;
    private final OutboxService outboxService;

    @Transactional
    public ChatSessionResponse createSession(UUID userId, CreateChatSessionRequest request) {
        ChatSessionEntity session = chatSessionRepository.save(new ChatSessionEntity(userId, title(request.title())));
        return toResponse(session);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionResponse> sessions(UUID userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> messages(UUID userId, UUID sessionId) {
        sessionFor(userId, sessionId);
        return chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ChatMessageResponse sendMessage(UUID userId, UUID sessionId, SendChatMessageRequest request) {
        ChatSessionEntity session = sessionFor(userId, sessionId);
        ChatMessageEntity userMessage = chatMessageRepository.save(new ChatMessageEntity(
                session.getId(),
                userId,
                ChatMessageRole.USER,
                request.content()
        ));
        enqueueMessageCreated(userMessage);
        List<ChatMessageEntity> context = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(session.getId());
        String assistantReply = medicalChatClient.reply(userId, session.getId(), request.content(), context);
        ChatMessageEntity assistantMessage = chatMessageRepository.save(new ChatMessageEntity(
                session.getId(),
                userId,
                ChatMessageRole.ASSISTANT,
                assistantReply
        ));
        enqueueMessageCreated(assistantMessage);
        session.markUpdated();
        chatSessionRepository.save(session);
        return toResponse(assistantMessage);
    }

    private ChatSessionEntity sessionFor(UUID userId, UUID sessionId) {
        return chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ChatSessionNotFoundException(sessionId));
    }

    private void enqueueMessageCreated(ChatMessageEntity message) {
        outboxService.enqueue(
                TopicNames.CHAT_EVENTS,
                EventTypes.CHAT_MESSAGE_CREATED,
                message.getId(),
                EventEnvelope.create(
                        EventTypes.CHAT_MESSAGE_CREATED,
                        UUID.randomUUID(),
                        message.getUserId(),
                        new ChatMessageCreatedEvent(message.getSessionId(), message.getId(), message.getRole().name())
                )
        );
    }

    private String title(String title) {
        if (title == null || title.isBlank()) {
            return DEFAULT_TITLE;
        }
        return title.trim();
    }

    private ChatSessionResponse toResponse(ChatSessionEntity session) {
        return new ChatSessionResponse(session.getId(), session.getTitle(), session.getCreatedAt(), session.getUpdatedAt());
    }

    private ChatMessageResponse toResponse(ChatMessageEntity message) {
        return new ChatMessageResponse(message.getId(), message.getSessionId(), message.getRole(), message.getContent(), message.getCreatedAt());
    }
}
