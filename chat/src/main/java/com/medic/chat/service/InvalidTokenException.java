package com.medic.chat.service;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException() {
        super("Invalid authorization token");
    }
}
