package com.medic.events.notification;

import java.util.UUID;

public record NotificationCreatedEvent(
        UUID notificationId,
        String title,
        String type
) {
}
