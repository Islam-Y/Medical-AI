package com.medic.consent.controller;

import com.medic.consent.service.ConsentNotFoundException;
import com.medic.consent.service.InvalidTokenException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

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
    }

    @Test
    void mapsMissingConsentToNotFound() {
        // Arrange
        ConsentNotFoundException exception = new ConsentNotFoundException(UUID.randomUUID());

        // Act
        var response = handler.notFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void mapsValidationErrorToBadRequest() {
        // Arrange
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "scope", "must not be null"));
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(mock(MethodParameter.class), bindingResult);

        // Act
        var response = handler.validation(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().message()).contains("scope");
    }
}
