package com.medic.consent.dto;

import com.medic.consent.entity.ConsentScope;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GrantConsentRequest(
        @NotNull ConsentScope scope,
        @Size(max = 255) String subject
) {
}
