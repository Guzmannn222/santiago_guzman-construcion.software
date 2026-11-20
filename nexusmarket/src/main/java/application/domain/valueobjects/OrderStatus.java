package application.domain.valueobjects;

import java.util.List;

/**
 * Lifecycle of an Order: PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED -> FINISHED (or CANCELLED before shipping).
 */
public final class OrderStatus extends DomainCatalog {

    public static final OrderStatus CART = new OrderStatus(
            "CART", "Cart", "Provisional selection (never persisted as an order).");
    public static final OrderStatus PENDING_PAYMENT = new OrderStatus(
            "PENDING_PAYMENT", "Pending Payment", "Order created and waiting for payment.");
    public static final OrderStatus PAID = new OrderStatus(
            "PAID", "Paid", "Payment confirmed.");
    public static final OrderStatus SHIPPED = new OrderStatus(
            "SHIPPED", "Shipped", "Order dispatched to the buyer.");
    public static final OrderStatus DELIVERED = new OrderStatus(
            "DELIVERED", "Delivered", "Order delivered to the buyer.");
    public static final OrderStatus FINISHED = new OrderStatus(
            "FINISHED", "Finished", "Order closed; it can never be modified again.");
    public static final OrderStatus CANCELLED = new OrderStatus(
            "CANCELLED", "Cancelled", "Order cancelled before shipping.");

    private static final List<OrderStatus> VALUES = List.of(CART, PENDING_PAYMENT, PAID, SHIPPED, DELIVERED, FINISHED, CANCELLED);

    private OrderStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<OrderStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static OrderStatus fromCode(String code) {
        return lookup("OrderStatus", code, VALUES);
    }
}
