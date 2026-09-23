package com.nexusmarket.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.nexusmarket.enums.OrderStatus;
import com.nexusmarket.util.InvalidOrderStateException;

/**
 * Formal commercial commitment generated from a confirmed cart (SDD, Domain 7).
 * Lifecycle: CART -&gt; PENDING_PAYMENT -&gt; PAID -&gt; SHIPPED -&gt; DELIVERED -&gt; FINISHED.
 */
public class Order {

    private final String id;
    private final Buyer buyer;
    private final List<OrderItem> items = new ArrayList<>();
    private double total;
    private OrderStatus status;
    private final LocalDateTime date;

    public Order(String id, Buyer buyer) {
        this.id = id;
        this.buyer = buyer;
        this.status = OrderStatus.PENDING_PAYMENT;
        this.date = LocalDateTime.now();
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        recalculateTotal();
    }

    private void recalculateTotal() {
        this.total = items.stream().mapToDouble(OrderItem::getSubtotal).sum();
    }

    public void confirmPayment() {
        changeStatus(OrderStatus.PAID);
    }

    public void ship() {
        changeStatus(OrderStatus.SHIPPED);
    }

    public void deliver() {
        changeStatus(OrderStatus.DELIVERED);
    }

    /** A finished order must never be modified under any circumstance (SDD 16.2). */
    public void changeStatus(OrderStatus newStatus) {
        if (!canModify()) {
            throw new InvalidOrderStateException("The order is finished and cannot be modified.");
        }
        this.status = newStatus;
    }

    public boolean canModify() {
        return this.status != OrderStatus.FINISHED;
    }

    public void finish() {
        this.status = OrderStatus.FINISHED;
    }

    /** Cancels the order. Only allowed before it has been shipped (SDD 16.2). */
    public void cancel() {
        if (this.status == OrderStatus.SHIPPED || this.status == OrderStatus.DELIVERED
                || this.status == OrderStatus.FINISHED) {
            throw new InvalidOrderStateException("An order that has already been shipped cannot be cancelled.");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public String getId() { return id; }
    public Buyer getBuyer() { return buyer; }
    public double getTotal() { return total; }
    public OrderStatus getStatus() { return status; }
    public LocalDateTime getDate() { return date; }
    public List<OrderItem> getItems() { return items; }
}
