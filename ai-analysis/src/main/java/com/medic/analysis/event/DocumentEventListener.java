package com.medic.analysis.event;

import com.medic.analysis.service.AnalysisJobService;
import com.medic.events.TopicNames;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

    private final AnalysisJobService analysisJobService;

    @KafkaListener(topics = TopicNames.DOCUMENT_EVENTS, groupId = "${spring.application.name}")
    public void handle(String message) {
        analysisJobService.handleDocumentEvent(message);
    }
}
