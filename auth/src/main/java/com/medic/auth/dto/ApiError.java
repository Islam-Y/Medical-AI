package com.medic.auth.dto;

public record ApiError(
        String code,
        String message
) {
}
