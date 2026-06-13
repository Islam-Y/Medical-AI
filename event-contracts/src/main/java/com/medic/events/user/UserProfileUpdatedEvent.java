package com.medic.events.user;

import java.util.UUID;

public record UserProfileUpdatedEvent(
        UUID profileId
) {
}
