package com.nexusmarket.util;

/**
 * Domain exception thrown when an inventory operation would leave negative stock
 * (critical business rule — see SDD sections 13 / 16.2).
 */
public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
