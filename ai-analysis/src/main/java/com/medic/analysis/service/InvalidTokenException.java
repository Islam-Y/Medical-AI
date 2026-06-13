package com.medic.analysis.service;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid authorization token");
    }
}
