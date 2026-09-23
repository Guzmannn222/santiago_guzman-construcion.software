package com.nexusmarket.model;

/** A single product line inside an {@link Order} (SDD, Domain 7). */
public class OrderItem {

    private final Product product;
    private final int quantity;
    private final double unitPrice;
    private double subtotal;

    public OrderItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public double calculateSubtotal() {
        this.subtotal = quantity * unitPrice;
        return this.subtotal;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public double getSubtotal() { return subtotal; }
}
