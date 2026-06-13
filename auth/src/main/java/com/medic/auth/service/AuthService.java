package com.medic.auth.service;

import com.medic.events.EventEnvelope;
import com.medic.events.EventTypes;
import com.medic.events.TopicNames;
import com.medic.events.user.UserRegisteredEvent;
import com.medic.auth.dto.AuthResponse;
import com.medic.auth.dto.LoginRequest;
import com.medic.auth.dto.RegisterRequest;
import com.medic.auth.dto.AuthUserResponse;
import com.medic.auth.entity.AuthUserEntity;
import com.medic.auth.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final OutboxService outboxService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (authUserRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateAccountException(email);
        }
        AuthUserEntity user = authUserRepository.save(new AuthUserEntity(email, passwordEncoder.encode(request.password())));
        outboxService.enqueue(
                TopicNames.AUTH_EVENTS,
                EventTypes.USER_REGISTERED,
                user.getId(),
                EventEnvelope.create(
                        EventTypes.USER_REGISTERED,
                        UUID.randomUUID(),
                        user.getId(),
                        new UserRegisteredEvent(user.getId(), user.getEmail())
                )
        );
        return authResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AuthUserEntity user = authUserRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return authResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthUserResponse currentUser(String authorization) {
        UUID userId = jwtService.parseBearerUserId(authorization);
        AuthUserEntity user = authUserRepository.findById(userId).orElseThrow(InvalidCredentialsException::new);
        return new AuthUserResponse(user.getId(), user.getEmail(), user.getCreatedAt());
    }

    private AuthResponse authResponse(AuthUserEntity user) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                jwtService.generate(user.getId(), user.getEmail()),
                jwtService.ttlSeconds()
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }
}
