package application.domain.models;

import application.domain.exceptions.DamagedInventoryException;
import application.domain.exceptions.InsufficientStockException;
import application.domain.exceptions.InvalidInventoryException;
import application.domain.exceptions.InvalidQuantityException;
import application.domain.valueobjects.InventoryStatus;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Controls stock for a product in a warehouse (distributed inventory).
 * Critical validations: stock can never go negative, and DAMAGED stock can never be reserved.
 */
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    public static final int DEFAULT_LOW_STOCK_THRESHOLD = 5;

    private Long id;
    private Product product;
    private Warehouse warehouse;
    private int stock;
    private InventoryStatus status = InventoryStatus.AVAILABLE;
    private List<InventoryMovement> movements = new ArrayList<>();
    /** Stock level (inclusive) at or below which the record is LOW_STOCK. Not persisted. */
    private int lowStockThreshold = DEFAULT_LOW_STOCK_THRESHOLD;

    public Inventory(Product product, Warehouse warehouse, int initialStock) {
        if (product == null) {
            throw new InvalidInventoryException("Inventory product must be provided.");
        }
        if (warehouse == null) {
            throw new InvalidInventoryException("Inventory warehouse must be provided.");
        }
        if (!product.isPhysical()) {
            throw new InvalidInventoryException("Only physical products have inventory.");
        }
        if (initialStock < 0) {
            throw new InvalidQuantityException("Initial stock cannot be negative.");
        }
        this.product = product;
        this.warehouse = warehouse;
        this.stock = initialStock;
        refreshStatus();
    }

    public void inbound(int quantity) {
        requirePositive(quantity);
        this.stock += quantity;
        refreshStatus();
    }

    /** Reserves stock for an order. Damaged or nonexistent stock can never be reserved. */
    public void reserve(int quantity) {
        requirePositive(quantity);
        if (InventoryStatus.DAMAGED.equals(this.status)) {
            throw new DamagedInventoryException(
                    "Inventory for product " + safeProductId() + " is marked as DAMAGED and cannot be reserved.");
        }
        if (quantity > this.stock) {
            throw new InsufficientStockException("Not enough stock for product " + safeProductId()
                    + " (requested " + quantity + ", available " + stock + ").");
        }
        this.stock -= quantity;
        refreshStatus();
    }

    public void withdraw(int quantity) {
        requirePositive(quantity);
        if (quantity > this.stock) {
            throw new InsufficientStockException("Not enough stock for product " + safeProductId()
                    + " (requested " + quantity + ", available " + stock + ").");
        }
        this.stock -= quantity;
        refreshStatus();
    }

    /** Sets an absolute new stock value after a physical count. Negative values are rejected. */
    public void adjust(int newQuantity) {
        if (newQuantity < 0) {
            throw new InvalidQuantityException("Stock can never be negative.");
        }
        this.stock = newQuantity;
        refreshStatus();
    }

    public void returnStock(int quantity) {
        requirePositive(quantity);
        this.stock += quantity;
        refreshStatus();
    }

    public void markDamaged() {
        if (InventoryStatus.DAMAGED.equals(this.status)) {
            throw new InvalidInventoryException("The inventory is already marked as damaged.");
        }
        this.status = InventoryStatus.DAMAGED;
    }

    public void clearDamaged() {
        if (!InventoryStatus.DAMAGED.equals(this.status)) {
            throw new InvalidInventoryException("The inventory is not marked as damaged.");
        }
        this.status = InventoryStatus.AVAILABLE;
        refreshStatus();
    }

    public boolean canFulfill(int quantity) {
        return !InventoryStatus.DAMAGED.equals(status) && quantity > 0 && quantity <= stock;
    }

    public void recordMovement(InventoryMovement movement) {
        if (movement == null) {
            throw new InvalidInventoryException("Movement must be provided.");
        }
        this.movements.add(movement);
    }

    /** Recomputes AVAILABLE / LOW_STOCK / OUT_OF_STOCK from the current quantity (DAMAGED is manual). */
    public void refreshStatus() {
        if (InventoryStatus.DAMAGED.equals(this.status)) {
            return;
        }
        if (this.stock <= 0) {
            this.status = InventoryStatus.OUT_OF_STOCK;
        } else if (this.stock <= lowStockThreshold) {
            this.status = InventoryStatus.LOW_STOCK;
        } else {
            this.status = InventoryStatus.AVAILABLE;
        }
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be greater than zero.");
        }
    }

    private String safeProductId() {
        return product == null ? "?" : String.valueOf(product.getId());
    }
}
