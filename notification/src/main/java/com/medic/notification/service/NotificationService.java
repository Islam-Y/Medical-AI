package com.medic.notification.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.notification.NotificationCreatedEvent;
import com.medic.notification.dto.NotificationResponse;
import com.medic.notification.entity.NotificationEntity;
import com.medic.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public List<NotificationResponse> notifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public NotificationResponse markRead(UUID userId, UUID notificationId) {
        NotificationEntity notification = notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
        notification.markRead();
        return toResponse(notification);
    }

    @Transactional
    public void handleHealthRecordEvent(String message) {
        JsonNode root = readTree(message);
        if (!EventTypes.HEALTH_RECORD_CHANGED.equals(root.path("eventType").asString())) {
            return;
        }
        UUID userId = UUID.fromString(root.path("userId").asString());
        String recordType = textOrDefault(root.path("payload").path("recordType"), "record");
        String changeType = textOrDefault(root.path("payload").path("changeType"), "changed");
        NotificationEntity notification = notificationRepository.save(new NotificationEntity(
                userId,
                "Health record updated",
                "Your " + recordType + " was " + changeType + ".",
                "HEALTH_RECORD"
        ));
        enqueueCreated(notification);
    }

    private void enqueueCreated(NotificationEntity notification) {
        outboxService.enqueue(
                TopicNames.NOTIFICATION_EVENTS,
                EventTypes.NOTIFICATION_CREATED,
                notification.getId(),
                EventEnvelope.create(
                        EventTypes.NOTIFICATION_CREATED,
                        UUID.randomUUID(),
                        notification.getUserId(),
                        new NotificationCreatedEvent(notification.getId(), notification.getTitle(), notification.getType())
                )
        );
    }

    private JsonNode readTree(String message) {
        try {
            return objectMapper.readTree(message);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Invalid event payload", exception);
        }
    }

    private String textOrDefault(JsonNode node, String defaultValue) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return defaultValue;
        }
        return node.asString();
    }

    private NotificationResponse toResponse(NotificationEntity notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}
