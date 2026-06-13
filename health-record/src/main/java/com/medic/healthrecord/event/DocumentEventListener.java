package com.medic.healthrecord.event;

import com.medic.events.TopicNames;
import com.medic.healthrecord.service.HealthRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

    private final HealthRecordService healthRecordService;

    @KafkaListener(topics = TopicNames.DOCUMENT_EVENTS, groupId = "${spring.application.name}")
    public void handle(String message) {
        healthRecordService.handleDocumentEvent(message);
    }
}
