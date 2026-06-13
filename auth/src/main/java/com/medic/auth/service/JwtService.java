package com.medic.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JwtService {

    private static final Base64.Encoder BASE64_URL_ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder BASE64_URL_DECODER = Base64.getUrlDecoder();
    private static final Pattern SUBJECT_PATTERN = Pattern.compile("\"sub\":\"([^\"]+)\"");
    private static final Pattern EXPIRATION_PATTERN = Pattern.compile("\"exp\":(\\d+)");
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;
    private final long ttlSeconds;
    private final Clock clock;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.ttl-seconds}") long ttlSeconds
    ) {
        this(secret, ttlSeconds, Clock.systemUTC());
    }

    JwtService(String secret, long ttlSeconds, Clock clock) {
        this.secret = secret;
        this.ttlSeconds = ttlSeconds;
        this.clock = clock;
    }

    public String generate(UUID userId, String email) {
        Instant now = Instant.now(clock);
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = "{\"sub\":\"%s\",\"email\":\"%s\",\"iat\":%d,\"exp\":%d}"
                .formatted(userId, escapeJson(email), now.getEpochSecond(), now.plusSeconds(ttlSeconds).getEpochSecond());
        String signingInput = encode(header) + "." + encode(payload);
        return signingInput + "." + sign(signingInput);
    }

    public UUID parseBearerUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new InvalidCredentialsException();
        }
        return parseUserId(authorization.substring("Bearer ".length()));
    }

    public UUID parseUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new InvalidCredentialsException();
        }
        String signingInput = parts[0] + "." + parts[1];
        if (!MessageDigest.isEqual(sign(signingInput).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidCredentialsException();
        }
        String payload = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        long expiresAt = extractLong(payload, EXPIRATION_PATTERN);
        if (Instant.now(clock).getEpochSecond() >= expiresAt) {
            throw new InvalidCredentialsException();
        }
        return UUID.fromString(extractString(payload, SUBJECT_PATTERN));
    }

    public long ttlSeconds() {
        return ttlSeconds;
    }

    private String encode(String value) {
        return BASE64_URL_ENCODER.encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT", exception);
        }
    }

    private String extractString(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            throw new InvalidCredentialsException();
        }
        return matcher.group(1);
    }

    private long extractLong(String payload, Pattern pattern) {
        return Long.parseLong(extractString(payload, pattern));
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
