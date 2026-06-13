package com.medic.auth.service;

public class DuplicateAccountException extends RuntimeException {

    public DuplicateAccountException(String email) {
        super("Account already exists for email: " + email);
    }
}
