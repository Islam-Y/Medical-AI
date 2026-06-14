package com.medic.events.indexing;

import java.util.UUID;

public record IndexEntryUpdatedEvent(
        UUID entryId,
        String sourceType,
        UUID sourceId
) {
}
