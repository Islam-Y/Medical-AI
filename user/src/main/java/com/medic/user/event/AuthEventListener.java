package com.medic.user.event;

import com.medic.events.TopicNames;
import com.medic.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthEventListener {

    private final UserProfileService userProfileService;

    @KafkaListener(topics = TopicNames.AUTH_EVENTS, groupId = "${spring.application.name}")
    public void handle(String message) {
        userProfileService.handleAuthEvent(message);
    }
}
