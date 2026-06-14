package com.medic.events.consent;

import java.util.UUID;

public record ConsentChangedEvent(
        UUID consentId,
        String scope,
        String status
) {
}
