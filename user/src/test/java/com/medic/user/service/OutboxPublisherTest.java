package com.medic.user.service;

import com.medic.user.entity.OutboxEvent;
import com.medic.user.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OutboxPublisherTest {

    @Test
    void publishPendingSendsEventsAndMarksPublished() {
        // Arrange
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        OutboxEvent event = new OutboxEvent("topic", "type", UUID.randomUUID(), "payload");
        when(repository.findTop20ByPublishedAtIsNullOrderByCreatedAtAsc()).thenReturn(List.of(event));
        OutboxPublisher publisher = new OutboxPublisher(repository, kafkaTemplate);

        // Act
        publisher.publishPending();

        // Assert
        verify(kafkaTemplate).send(event.getTopic(), event.getAggregateId().toString(), event.getPayload());
        assertThat(event.getPublishedAt()).isNotNull();
    }
}
