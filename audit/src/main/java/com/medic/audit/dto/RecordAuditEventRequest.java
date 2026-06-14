package com.medic.audit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecordAuditEventRequest(
        @NotBlank @Size(max = 120) String action,
        @NotBlank @Size(max = 120) String resourceType,
        @Size(max = 120) String resourceId,
        @Size(max = 4000) String metadata
) {
}
