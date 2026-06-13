package com.medic.healthrecord.dto;

public record ApiError(
        String code,
        String message
) {
}
