package com.medic.auth.service;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-13T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void parsesGeneratedBearerToken() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", 3600, CLOCK);
        UUID userId = UUID.randomUUID();
        String token = service.generate(userId, "test@example.com");

        // Act
        UUID parsedUserId = service.parseBearerUserId("Bearer " + token);

        // Assert
        assertThat(parsedUserId).isEqualTo(userId);
    }

    @Test
    void exposesConfiguredTtl() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", 3600);

        // Act
        long ttl = service.ttlSeconds();

        // Assert
        assertThat(ttl).isEqualTo(3600);
    }

    @Test
    void rejectsMalformedBearerToken() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", 3600, CLOCK);

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseBearerUserId("Basic bad"))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsTamperedToken() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", 3600, CLOCK);
        String token = service.generate(UUID.randomUUID(), "test@example.com") + "x";

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseUserId(token))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void rejectsTokenWithInvalidShape() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", 3600, CLOCK);

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseUserId("bad.token"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
