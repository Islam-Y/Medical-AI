package com.medic.notification.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.health.HealthRecordChangedEvent;
import com.medic.notification.dto.NotificationResponse;
import com.medic.notification.entity.NotificationEntity;
import com.medic.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final OutboxService outboxService = mock(OutboxService.class);
    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(notificationRepository, outboxService, new ObjectMapper());
    }

    @Test
    void notificationsReturnsUserNotifications() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationEntity notification = new NotificationEntity(userId, "Title", "Message", "TYPE");
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(notification));

        // Act
        List<NotificationResponse> response = service.notifications(userId);

        // Assert
        assertThat(response).extracting(NotificationResponse::title).containsExactly("Title");
    }

    @Test
    void markReadUpdatesNotification() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationEntity notification = new NotificationEntity(userId, "Title", "Message", "TYPE");
        when(notificationRepository.findByIdAndUserId(notification.getId(), userId)).thenReturn(Optional.of(notification));

        // Act
        NotificationResponse response = service.markRead(userId, notification.getId());

        // Assert
        assertThat(response.read()).isTrue();
        assertThat(response.readAt()).isNotNull();
    }

    @Test
    void markReadRejectsMissingNotification() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        when(notificationRepository.findByIdAndUserId(notificationId, userId)).thenReturn(Optional.empty());

        // Act
        // Assert
        assertThatThrownBy(() -> service.markRead(userId, notificationId))
                .isInstanceOf(NotificationNotFoundException.class);
    }

    @Test
    void handleHealthRecordEventCreatesNotificationAndOutboxEvent() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                EventTypes.HEALTH_RECORD_CHANGED,
                UUID.randomUUID(),
                userId,
                new HealthRecordChangedEvent(UUID.randomUUID(), "observation", "created")
        ));
        when(notificationRepository.save(any(NotificationEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.handleHealthRecordEvent(message);

        // Assert
        verify(notificationRepository).save(any(NotificationEntity.class));
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void handleHealthRecordEventIgnoresOtherEvents() throws Exception {
        // Arrange
        String message = new ObjectMapper().writeValueAsString(EventEnvelope.create(
                "OtherEvent",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Object()
        ));

        // Act
        service.handleHealthRecordEvent(message);

        // Assert
        org.mockito.Mockito.verifyNoInteractions(outboxService);
    }
}
