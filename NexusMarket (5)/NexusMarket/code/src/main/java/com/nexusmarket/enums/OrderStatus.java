package com.nexusmarket.enums;

/**
 * Lifecycle of an {@link com.nexusmarket.model.Order}.
 * Strict sequence: CART -&gt; PENDING_PAYMENT -&gt; PAID -&gt; SHIPPED -&gt; DELIVERED -&gt; FINISHED.
 */
public enum OrderStatus {
    CART,
    PENDING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    FINISHED,
    CANCELLED
}
