package com.medic.healthrecord.service;

import com.medic.healthrecord.entity.OutboxEvent;
import com.medic.healthrecord.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.publisher.fixed-delay-ms}")
    @Transactional
    public void publishPending() {
        for (OutboxEvent event : outboxEventRepository.findTop20ByPublishedAtIsNullOrderByCreatedAtAsc()) {
            kafkaTemplate.send(event.getTopic(), event.getAggregateId().toString(), event.getPayload());
            event.markPublished();
        }
    }
}
