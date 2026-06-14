package com.medic.retrieval.dto;

public record ApiError(
        String code,
        String message
) {
}
