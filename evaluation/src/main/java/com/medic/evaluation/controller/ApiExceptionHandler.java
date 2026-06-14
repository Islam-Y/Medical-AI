package com.medic.evaluation.controller;

import com.medic.evaluation.dto.ApiError;
import com.medic.evaluation.service.EvaluationRunNotFoundException;
import com.medic.evaluation.service.InvalidTokenException;
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

    @ExceptionHandler(EvaluationRunNotFoundException.class)
    ResponseEntity<ApiError> notFound(EvaluationRunNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("evaluation_run_not_found", exception.getMessage()));
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
