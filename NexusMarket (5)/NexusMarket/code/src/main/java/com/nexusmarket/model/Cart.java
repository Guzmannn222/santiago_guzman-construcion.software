package com.nexusmarket.model;

import java.util.ArrayList;
import java.util.List;

/** Provisional product selection made by a {@link Buyer} (SDD, Domain 7). */
public class Cart {

    private final String id;
    private final Buyer buyer;
    private final List<CartItem> items = new ArrayList<>();
    private double total;

    public Cart(String id, Buyer buyer) {
        this.id = id;
        this.buyer = buyer;
    }

    public void addProduct(Product product, int quantity) {
        items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> items.add(new CartItem(product, quantity, product.getPrice())));
        calculateTotal();
    }

    public void removeProduct(Product product) {
        items.removeIf(item -> item.getProduct().equals(product));
        calculateTotal();
    }

    public void updateQuantity(Product product, int quantity) {
        items.stream()
                .filter(item -> item.getProduct().equals(product))
                .findFirst()
                .ifPresent(item -> item.setQuantity(quantity));
        calculateTotal();
    }

    public double calculateTotal() {
        this.total = items.stream().mapToDouble(CartItem::getLineTotal).sum();
        return this.total;
    }

    /** Confirms the cart so an {@link Order} can be created from it (CU-09). */
    public void confirm() {
        if (items.isEmpty()) {
            throw new IllegalStateException("The cart must contain at least one product.");
        }
    }

    public String getId() { return id; }
    public Buyer getBuyer() { return buyer; }
    public List<CartItem> getItems() { return items; }
    public double getTotal() { return total; }
}
