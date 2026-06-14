package com.medic.indexing.dto;

public record ApiError(
        String code,
        String message
) {
}
