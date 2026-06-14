package com.medic.audit.service;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid authorization token");
    }
}
