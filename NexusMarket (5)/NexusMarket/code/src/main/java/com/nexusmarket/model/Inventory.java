package com.nexusmarket.model;

import java.util.ArrayList;
import java.util.List;

import com.nexusmarket.util.InsufficientStockException;

/**
 * Controls stock for a product in a warehouse (SDD, Domain 6).
 * Critical constraint: negative stock is never allowed under any circumstance.
 */
public class Inventory {

    private final Product product;
    private final Warehouse warehouse;
    private int stock;
    private final List<InventoryMovement> movements = new ArrayList<>();

    public Inventory(Product product, Warehouse warehouse, int initialStock) {
        this.product = product;
        this.warehouse = warehouse;
        this.stock = initialStock;
    }

    /** Adds stock that has entered the warehouse (e.g. from a purchase order). */
    public void inbound(int quantity) {
        requirePositive(quantity);
        this.stock += quantity;
    }

    /** Reserves stock for an order. Stock that does not exist cannot be reserved (SDD 16.2). */
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (quantity > this.stock) {
            throw new InsufficientStockException(
                    "Not enough stock for product " + product.getId() + " in warehouse " + warehouse.getId());
        }
        this.stock -= quantity;
    }

    /** Withdraws stock permanently, e.g. after a confirmed sale is shipped. */
    public void withdraw(int quantity) {
        requirePositive(quantity);
        if (quantity > this.stock) {
            throw new InsufficientStockException(
                    "Not enough stock for product " + product.getId() + " in warehouse " + warehouse.getId());
        }
        this.stock -= quantity;
    }

    /** Sets an absolute new stock value, e.g. after a physical count (never negative). */
    public void adjust(int newQuantity) {
        this.stock = Math.max(0, newQuantity);
    }

    /** Returns stock to the warehouse, e.g. after an approved product return. */
    public void returnStock(int quantity) {
        requirePositive(quantity);
        this.stock += quantity;
    }

    public void recordMovement(InventoryMovement movement) {
        this.movements.add(movement);
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    public Product getProduct() { return product; }
    public Warehouse getWarehouse() { return warehouse; }
    public int getStock() { return stock; }
    public List<InventoryMovement> getMovements() { return movements; }
}
