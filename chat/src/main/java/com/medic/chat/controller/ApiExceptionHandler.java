package com.medic.chat.controller;

import com.medic.chat.dto.ApiError;
import com.medic.chat.service.ChatSessionNotFoundException;
import com.medic.chat.service.InvalidTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    ResponseEntity<ApiError> invalidToken(InvalidTokenException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiError("invalid_token", exception.getMessage()));
    }

    @ExceptionHandler(ChatSessionNotFoundException.class)
    ResponseEntity<ApiError> notFound(ChatSessionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("chat_session_not_found", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("Request validation failed");
        return ResponseEntity.badRequest().body(new ApiError("validation_error", message));
    }
}
