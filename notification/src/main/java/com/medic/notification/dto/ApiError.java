package com.medic.notification.dto;

public record ApiError(
        String code,
        String message
) {
}
