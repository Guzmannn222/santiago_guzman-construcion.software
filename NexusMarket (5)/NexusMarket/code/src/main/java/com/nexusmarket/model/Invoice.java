package com.nexusmarket.model;

/** Commercial billing information tied to an {@link Order} (SDD, Domain 8). */
public class Invoice {

    private final String id;
    private final Order order;
    private double subtotal;
    private double tax;
    private double total;
    private boolean cancelled;

    public Invoice(String id, Order order) {
        this.id = id;
        this.order = order;
    }

    public void generate(double taxRate) {
        this.subtotal = order.getTotal();
        this.tax = this.subtotal * taxRate;
        calculateTotal();
    }

    public double calculateTotal() {
        this.total = this.subtotal + this.tax;
        return this.total;
    }

    public void cancel() {
        this.cancelled = true;
        this.total = 0;
    }

    public String getId() { return id; }
    public Order getOrder() { return order; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getTotal() { return total; }
    public boolean isCancelled() { return cancelled; }
}
