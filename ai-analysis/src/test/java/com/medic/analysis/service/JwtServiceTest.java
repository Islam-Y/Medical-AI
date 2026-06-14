package com.medic.analysis.service;

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
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-06-13T10:00:00Z"), ZoneOffset.UTC);

    @Test
    void parsesValidBearerToken() {
        // Arrange
        String secret = "secret-secret-secret-secret";
        UUID userId = UUID.randomUUID();
        JwtService service = new JwtService(secret, CLOCK);
        String token = token(secret, userId, CLOCK.instant().plusSeconds(3600).getEpochSecond());

        // Act
        UUID parsedUserId = service.parseBearerUserId("Bearer " + token);

        // Assert
        assertThat(parsedUserId).isEqualTo(userId);
    }

    @Test
    void rejectsMissingBearerPrefix() {
        // Arrange
        JwtService service = new JwtService("secret-secret-secret-secret", CLOCK);

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseBearerUserId("Basic bad"))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsExpiredToken() {
        // Arrange
        String secret = "secret-secret-secret-secret";
        JwtService service = new JwtService(secret, CLOCK);
        String token = token(secret, UUID.randomUUID(), CLOCK.instant().minusSeconds(1).getEpochSecond());

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseUserId(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void rejectsTamperedToken() {
        // Arrange
        String secret = "secret-secret-secret-secret";
        JwtService service = new JwtService(secret, CLOCK);
        String token = token(secret, UUID.randomUUID(), CLOCK.instant().plusSeconds(3600).getEpochSecond()) + "x";

        // Act
        // Assert
        assertThatThrownBy(() -> service.parseUserId(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    private static String token(String secret, UUID userId, long expiration) {
        String header = BASE64_URL_ENCODER.encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
        String payload = BASE64_URL_ENCODER.encodeToString(("{\"sub\":\"" + userId + "\",\"exp\":" + expiration + "}").getBytes(StandardCharsets.UTF_8));
        String signingInput = header + "." + payload;
        return signingInput + "." + sign(secret, signingInput);
    }

    private static String sign(String secret, String signingInput) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
