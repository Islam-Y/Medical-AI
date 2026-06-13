package com.medic.notification.event;

import com.medic.events.TopicNames;
import com.medic.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthRecordEventListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = TopicNames.HEALTH_RECORD_EVENTS, groupId = "${spring.application.name}")
    public void handle(String message) {
        notificationService.handleHealthRecordEvent(message);
    }
}
