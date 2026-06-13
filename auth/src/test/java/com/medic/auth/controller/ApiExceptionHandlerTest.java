package com.medic.auth.controller;

import com.medic.auth.service.DuplicateAccountException;
import com.medic.auth.service.InvalidCredentialsException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void mapsDuplicateEmailToConflict() {
        // Arrange
        DuplicateAccountException exception = new DuplicateAccountException("test@example.com");

        // Act
        var response = handler.duplicateEmail(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().code()).isEqualTo("duplicate_account");
    }

    @Test
    void mapsInvalidCredentialsToUnauthorized() {
        // Arrange
        InvalidCredentialsException exception = new InvalidCredentialsException();

        // Act
        var response = handler.invalidCredentials(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().code()).isEqualTo("invalid_credentials");
    }
}
