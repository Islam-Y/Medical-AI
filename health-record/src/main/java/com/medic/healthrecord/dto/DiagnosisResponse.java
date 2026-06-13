package com.medic.healthrecord.dto;

import java.time.Instant;
import java.util.UUID;

public record DiagnosisResponse(
        UUID id,
        String name,
        Instant diagnosedAt,
        String source
) {
}
