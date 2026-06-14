package com.medic.audit.dto;

public record ApiError(
        String code,
        String message
) {
}
