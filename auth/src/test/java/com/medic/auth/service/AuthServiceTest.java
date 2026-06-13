package com.medic.auth.service;

import com.medic.auth.dto.AuthResponse;
import com.medic.auth.dto.LoginRequest;
import com.medic.auth.dto.RegisterRequest;
import com.medic.auth.dto.AuthUserResponse;
import com.medic.auth.entity.AuthUserEntity;
import com.medic.auth.repository.AuthUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthUserRepository authUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCreatesUserAndOutboxEvent() {
        // Arrange
        RegisterRequest request = new RegisterRequest(" Test@Example.com ", "password123");
        when(authUserRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(authUserRepository.save(any(AuthUserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generate(any(UUID.class), org.mockito.ArgumentMatchers.eq("test@example.com"))).thenReturn("jwt");
        when(jwtService.ttlSeconds()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.register(request);

        // Assert
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.accessToken()).isEqualTo("jwt");
        ArgumentCaptor<AuthUserEntity> userCaptor = ArgumentCaptor.forClass(AuthUserEntity.class);
        verify(authUserRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hash");
        verify(outboxService).enqueue(any(), any(), any(), any());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");
        when(authUserRepository.existsByEmailIgnoreCase("test@example.com")).thenReturn(true);

        // Act
        // Assert
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateAccountException.class);
        verifyNoInteractions(passwordEncoder, outboxService);
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        // Arrange
        AuthUserEntity user = new AuthUserEntity("test@example.com", "hash");
        when(authUserRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
        when(jwtService.generate(user.getId(), "test@example.com")).thenReturn("jwt");
        when(jwtService.ttlSeconds()).thenReturn(3600L);

        // Act
        AuthResponse response = authService.login(new LoginRequest("test@example.com", "password123"));

        // Assert
        assertThat(response.userId()).isEqualTo(user.getId());
        assertThat(response.accessToken()).isEqualTo("jwt");
    }

    @Test
    void loginRejectsWrongPassword() {
        // Arrange
        AuthUserEntity user = new AuthUserEntity("test@example.com", "hash");
        when(authUserRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        // Act
        // Assert
        assertThatThrownBy(() -> authService.login(new LoginRequest("test@example.com", "wrong")))
                .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void currentUserLoadsUserFromBearerToken() {
        // Arrange
        AuthUserEntity user = new AuthUserEntity("test@example.com", "hash");
        when(jwtService.parseBearerUserId("Bearer token")).thenReturn(user.getId());
        when(authUserRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        AuthUserResponse response = authService.currentUser("Bearer token");

        // Assert
        assertThat(response.id()).isEqualTo(user.getId());
        assertThat(response.email()).isEqualTo("test@example.com");
    }
}
