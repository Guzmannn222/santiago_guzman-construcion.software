package com.nexusmarket.model;

/** A single product line inside a {@link Cart} (SDD, Domain 7). */
public class CartItem {

    private final Product product;
    private int quantity;
    private final double unitPrice;

    public CartItem(Product product, int quantity, double unitPrice) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public double getLineTotal() {
        return quantity * unitPrice;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getUnitPrice() { return unitPrice; }
}
