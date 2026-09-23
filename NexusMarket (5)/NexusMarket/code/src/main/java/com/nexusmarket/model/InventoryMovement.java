package com.nexusmarket.model;

import java.time.LocalDateTime;

import com.nexusmarket.enums.InventoryMovementType;

/** Records a change of stock over an {@link Inventory} record (SDD, Domain 6). */
public class InventoryMovement {

    private final String id;
    private final InventoryMovementType type;
    private final int quantity;
    private final LocalDateTime date;
    private final String reason;

    public InventoryMovement(String id, InventoryMovementType type, int quantity, String reason) {
        this.id = id;
        this.type = type;
        this.quantity = quantity;
        this.reason = reason;
        this.date = LocalDateTime.now();
    }

    public String getId() { return id; }
    public InventoryMovementType getType() { return type; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getDate() { return date; }
    public String getReason() { return reason; }
}
