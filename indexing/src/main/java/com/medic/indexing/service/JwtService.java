package com.medic.indexing.service;

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
    private final Clock clock;

    public JwtService(@Value("${security.jwt.secret}") String secret) {
        this(secret, Clock.systemUTC());
    }

    JwtService(String secret, Clock clock) {
        this.secret = secret;
        this.clock = clock;
    }

    public UUID parseBearerUserId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }
        return parseUserId(authorization.substring("Bearer ".length()));
    }

    UUID parseUserId(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new InvalidTokenException();
        }
        String signingInput = parts[0] + "." + parts[1];
        if (!MessageDigest.isEqual(sign(signingInput).getBytes(StandardCharsets.UTF_8), parts[2].getBytes(StandardCharsets.UTF_8))) {
            throw new InvalidTokenException();
        }
        String payload = new String(BASE64_URL_DECODER.decode(parts[1]), StandardCharsets.UTF_8);
        if (Instant.now(clock).getEpochSecond() >= extractLong(payload, EXPIRATION_PATTERN)) {
            throw new InvalidTokenException();
        }
        return UUID.fromString(extractString(payload, SUBJECT_PATTERN));
    }

    private String sign(String signingInput) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));
            return BASE64_URL_ENCODER.encodeToString(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to verify JWT", exception);
        }
    }

    private String extractString(String payload, Pattern pattern) {
        Matcher matcher = pattern.matcher(payload);
        if (!matcher.find()) {
            throw new InvalidTokenException();
        }
        return matcher.group(1);
    }

    private long extractLong(String payload, Pattern pattern) {
        return Long.parseLong(extractString(payload, pattern));
    }
}
