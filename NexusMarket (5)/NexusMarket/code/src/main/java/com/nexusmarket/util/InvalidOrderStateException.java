package com.nexusmarket.util;

/**
 * Domain exception thrown when an operation is attempted on an {@link com.nexusmarket.model.Order}
 * that is not in a valid state to allow it (e.g. modifying a finished order).
 */
public class InvalidOrderStateException extends RuntimeException {
    public InvalidOrderStateException(String message) {
        super(message);
    }
}
