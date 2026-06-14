package com.medic.consent.service;

import java.util.UUID;

public class ConsentNotFoundException extends RuntimeException {

    public ConsentNotFoundException(UUID id) {
        super("Consent not found: " + id);
    }
}
