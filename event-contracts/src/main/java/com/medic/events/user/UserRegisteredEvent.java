package com.medic.events.user;

import java.util.UUID;

public record UserRegisteredEvent(
        UUID userId,
        String email
) {
}
