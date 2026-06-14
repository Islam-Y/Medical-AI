package com.medic.chat.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class StubMedicalChatClientTest {

    @Test
    void returnsDeterministicStubReply() {
        // Arrange
        StubMedicalChatClient client = new StubMedicalChatClient();

        // Act
        String reply = client.reply(UUID.randomUUID(), UUID.randomUUID(), "Hello", List.of());

        // Assert
        assertThat(reply).contains("stub");
    }
}
