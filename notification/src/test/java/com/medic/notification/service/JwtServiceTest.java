package com.medic.notification.service;

import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    @Test
    void parsesValidBearerToken() throws Exception {
        // Arrange
        UUID userId = UUID.randomUUID();
        JwtService service = new JwtService("secret", Clock.fixed(Instant.parse("2026-06-13T10:00:00Z"), ZoneOffset.UTC));

        // Act
        UUID parsed = service.parseBearerUserId("Bearer " + token(userId, "secret", 2_000_000_000L));

        // Assert
        assertThat(parsed).isEqualTo(userId);
    }

    @Test
    void rejectsMissingBearerPrefix() {
        // Arrange
        JwtService service = new JwtService("secret", Clock.fixed(Instant.parse("2026-06-13T10:00:00Z"), ZoneOffset.UTC));

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseBearerUserId("token"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsExpiredToken() throws Exception {
        // Arrange
        JwtService service = new JwtService("secret", Clock.fixed(Instant.parse("2026-06-13T10:00:00Z"), ZoneOffset.UTC));
        String token = token(UUID.randomUUID(), "secret", 1L);

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseBearerUserId("Bearer " + token))
                .isInstanceOf(InvalidTokenException.class);
    }

    private String token(UUID userId, String secret, long exp) throws Exception {
        String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        String payload = encode("{\"sub\":\"%s\",\"exp\":%d}".formatted(userId, exp));
        String signingInput = header + "." + payload;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return signingInput + "." + BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
    }

    private String encode(String value) {
        return BASE64_URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
