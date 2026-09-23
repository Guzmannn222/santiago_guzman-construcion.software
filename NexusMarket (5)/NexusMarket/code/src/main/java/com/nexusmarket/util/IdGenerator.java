package com.nexusmarket.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Simple, dependency-free identifier generator used across the domain while the project does
 * not yet rely on a real persistence engine capable of generating its own keys.
 */
public final class IdGenerator {

    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    private IdGenerator() {
    }

    /** Generates a readable, incremental identifier prefixed with the given entity code. */
    public static String next(String prefix) {
        return prefix + "-" + SEQUENCE.getAndIncrement();
    }
}
