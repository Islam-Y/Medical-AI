package com.medic.evaluation.dto;

public record ApiError(
        String code,
        String message
) {
}
