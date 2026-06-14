package com.medic.chat.dto;

public record ApiError(
        String code,
        String message
) {
}
