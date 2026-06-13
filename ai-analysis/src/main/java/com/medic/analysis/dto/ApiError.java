package com.medic.analysis.dto;

public record ApiError(
        String code,
        String message
) {
}
