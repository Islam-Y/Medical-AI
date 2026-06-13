package com.medic.document.controller;

import com.medic.document.dto.ApiError;
import com.medic.document.service.DocumentNotFoundException;
import com.medic.document.service.InvalidTokenException;
import com.medic.document.service.StorageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ApiError> invalidToken(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("invalid_token", exception.getMessage()));
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    ResponseEntity<ApiError> notFound(DocumentNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("document_not_found", exception.getMessage()));
    }

    @ExceptionHandler(StorageException.class)
    ResponseEntity<ApiError> storage(StorageException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError("storage_error", exception.getMessage()));
    }
}
