package com.medic.notification.controller;

import com.medic.notification.dto.ApiError;
import com.medic.notification.service.InvalidTokenException;
import com.medic.notification.service.NotificationNotFoundException;
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

    @ExceptionHandler(NotificationNotFoundException.class)
    ResponseEntity<ApiError> notFound(NotificationNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("notification_not_found", exception.getMessage()));
    }
}
