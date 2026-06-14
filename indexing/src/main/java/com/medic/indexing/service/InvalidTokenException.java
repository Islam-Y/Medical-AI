package com.medic.indexing.service;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid authorization token");
    }
}
