package com.nexusmarket.util;

/**
 * Domain exception thrown when trying to register a {@link com.nexusmarket.model.User}
 * with an email address that is already in use (SDD section 31.1 — data integrity).
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
