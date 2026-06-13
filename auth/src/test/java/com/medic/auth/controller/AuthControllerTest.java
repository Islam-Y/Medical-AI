package com.medic.auth.controller;

import com.medic.auth.dto.AuthResponse;
import com.medic.auth.dto.LoginRequest;
import com.medic.auth.dto.RegisterRequest;
import com.medic.auth.dto.AuthUserResponse;
import com.medic.auth.service.AuthService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    private final AuthService authService = mock(AuthService.class);
    private final AuthController controller = new AuthController(authService);

    @Test
    void registerDelegatesToService() {
        // Arrange
        RegisterRequest request = new RegisterRequest("test@example.com", "password123");
        AuthResponse expected = new AuthResponse(UUID.randomUUID(), "test@example.com", "token", 3600);
        when(authService.register(request)).thenReturn(expected);

        // Act
        AuthResponse response = controller.register(request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(authService).register(request);
    }

    @Test
    void loginDelegatesToService() {
        // Arrange
        LoginRequest request = new LoginRequest("test@example.com", "password123");
        AuthResponse expected = new AuthResponse(UUID.randomUUID(), "test@example.com", "token", 3600);
        when(authService.login(request)).thenReturn(expected);

        // Act
        AuthResponse response = controller.login(request);

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(authService).login(request);
    }

    @Test
    void meDelegatesToService() {
        // Arrange
        AuthUserController userController = new AuthUserController(authService);
        AuthUserResponse expected = new AuthUserResponse(UUID.randomUUID(), "test@example.com", Instant.now());
        when(authService.currentUser("Bearer token")).thenReturn(expected);

        // Act
        AuthUserResponse response = userController.me("Bearer token");

        // Assert
        assertThat(response).isEqualTo(expected);
        verify(authService).currentUser("Bearer token");
    }
}
