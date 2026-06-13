package com.medic.document.dto;

public record ApiError(
        String code,
        String message
) {
}
