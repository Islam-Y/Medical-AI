package com.medic.notification.controller;

import com.medic.notification.service.InvalidTokenException;
import com.medic.notification.service.NotificationNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsInvalidTokenToUnauthorized() {
        // Arrange
        InvalidTokenException exception = new InvalidTokenException();

        // Act
        var response = handler.invalidToken(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("invalid_token");
    }

    @Test
    void mapsMissingNotificationToNotFound() {
        // Arrange
        NotificationNotFoundException exception = new NotificationNotFoundException(UUID.randomUUID());

        // Act
        var response = handler.notFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("notification_not_found");
    }
}
