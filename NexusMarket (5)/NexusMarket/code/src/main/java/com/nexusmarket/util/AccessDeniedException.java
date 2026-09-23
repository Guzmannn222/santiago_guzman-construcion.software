package com.nexusmarket.util;

/**
 * Domain exception thrown when a {@link com.nexusmarket.model.User} tries to execute a service
 * that their {@link com.nexusmarket.enums.Role} is not authorized to perform, or when the user
 * account is not active (RG-01, RG-02 — see SDD section 16.1).
 */
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
