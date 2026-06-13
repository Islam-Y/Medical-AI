package com.medic.notification.controller;

import com.medic.notification.dto.NotificationResponse;
import com.medic.notification.service.JwtService;
import com.medic.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationControllerTest {

    private final NotificationService notificationService = mock(NotificationService.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final NotificationController controller = new NotificationController(notificationService, jwtService);

    @Test
    void notificationsUseAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        NotificationResponse notification = new NotificationResponse(UUID.randomUUID(), "Title", "Message", "TYPE", false, Instant.now(), null);
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(notificationService.notifications(userId)).thenReturn(List.of(notification));

        // Act
        List<NotificationResponse> response = controller.notifications("Bearer token");

        // Assert
        assertThat(response).containsExactly(notification);
        verify(notificationService).notifications(userId);
    }

    @Test
    void markReadUsesAuthenticatedUser() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UUID notificationId = UUID.randomUUID();
        NotificationResponse notification = new NotificationResponse(notificationId, "Title", "Message", "TYPE", true, Instant.now(), Instant.now());
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(userId);
        when(notificationService.markRead(userId, notificationId)).thenReturn(notification);

        // Act
        NotificationResponse response = controller.markRead("Bearer token", notificationId);

        // Assert
        assertThat(response).isEqualTo(notification);
        verify(notificationService).markRead(userId, notificationId);
    }
}
