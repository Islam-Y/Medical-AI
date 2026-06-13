package com.medic.user.dto;

public record ApiError(
        String code,
        String message
) {
}
