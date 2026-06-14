package com.medic.analysis.controller;

import com.medic.analysis.dto.ApiError;
import com.medic.analysis.service.AnalysisJobNotFoundException;
import com.medic.analysis.service.InvalidTokenException;
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

    @ExceptionHandler(AnalysisJobNotFoundException.class)
    ResponseEntity<ApiError> notFound(AnalysisJobNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError("analysis_job_not_found", exception.getMessage()));
    }
}
