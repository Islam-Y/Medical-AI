package com.medic.indexing.service;

import java.util.UUID;

public class IndexEntryNotFoundException extends RuntimeException {

    public IndexEntryNotFoundException(UUID id) {
        super("Index entry not found: " + id);
    }
}
