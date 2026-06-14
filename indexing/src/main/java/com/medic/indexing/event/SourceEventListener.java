package com.medic.indexing.event;

import com.medic.events.TopicNames;
import com.medic.indexing.service.IndexingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SourceEventListener {

    private final IndexingService indexingService;

    @KafkaListener(topics = {
            TopicNames.DOCUMENT_EVENTS,
            TopicNames.HEALTH_RECORD_EVENTS,
            TopicNames.CHAT_EVENTS
    }, groupId = "${spring.application.name}")
    public void handle(String message) {
        indexingService.handleSourceEvent(message);
    }
}
