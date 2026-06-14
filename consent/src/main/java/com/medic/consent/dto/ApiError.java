package com.medic.consent.dto;

public record ApiError(
        String code,
        String message
) {
}
