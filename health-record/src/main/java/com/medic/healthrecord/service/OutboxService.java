package com.medic.healthrecord.service;

import com.medic.events.EventEnvelope;
import com.medic.healthrecord.entity.OutboxEvent;
import com.medic.healthrecord.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void enqueue(String topic, String eventType, UUID aggregateId, EventEnvelope<?> event) {
        try {
            outboxEventRepository.save(new OutboxEvent(topic, eventType, aggregateId, objectMapper.writeValueAsString(event)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to serialize outbox event", exception);
        }
    }
}
