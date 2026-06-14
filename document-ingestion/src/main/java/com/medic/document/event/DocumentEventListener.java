package com.medic.document.event;

import com.medic.document.service.DocumentService;
import com.medic.events.TopicNames;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

    private final DocumentService documentService;

    @KafkaListener(topics = TopicNames.DOCUMENT_EVENTS, groupId = "${spring.application.name}")
    public void handle(String message) {
        documentService.handleDocumentEvent(message);
    }
}
