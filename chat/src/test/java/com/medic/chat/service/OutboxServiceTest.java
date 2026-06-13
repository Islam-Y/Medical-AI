package com.medic.chat.service;

import com.medic.chat.entity.OutboxEvent;
import com.medic.chat.repository.OutboxEventRepository;
import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class OutboxServiceTest {

    @Test
    void enqueueSerializesEventPayload() {
        // Arrange
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        OutboxService service = new OutboxService(repository, new ObjectMapper());
        UUID userId = UUID.randomUUID();

        // Act
        service.enqueue("topic", EventTypes.CHAT_MESSAGE_CREATED, userId, EventEnvelope.create(EventTypes.CHAT_MESSAGE_CREATED, UUID.randomUUID(), userId, "payload"));

        // Assert
        ArgumentCaptor<OutboxEvent> captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getPayload()).contains(EventTypes.CHAT_MESSAGE_CREATED);
    }
}
