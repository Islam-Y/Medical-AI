package com.medic.evaluation.service;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid authorization token");
    }
}
