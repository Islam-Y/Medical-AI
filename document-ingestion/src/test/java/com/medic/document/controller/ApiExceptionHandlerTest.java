package com.medic.document.controller;

import com.medic.document.service.DocumentNotFoundException;
import com.medic.document.service.InvalidTokenException;
import com.medic.document.service.StorageException;
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
    void mapsMissingDocumentToNotFound() {
        // Arrange
        DocumentNotFoundException exception = new DocumentNotFoundException(UUID.randomUUID());

        // Act
        var response = handler.notFound(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().code()).isEqualTo("document_not_found");
    }

    @Test
    void mapsStorageErrorToBadRequest() {
        // Arrange
        StorageException exception = new StorageException("bad file", new IllegalStateException("bad"));

        // Act
        var response = handler.storage(exception);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().code()).isEqualTo("storage_error");
    }
}
