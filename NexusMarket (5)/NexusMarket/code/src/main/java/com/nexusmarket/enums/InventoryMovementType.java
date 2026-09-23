package com.nexusmarket.enums;

/** Types of movement recorded against an {@link com.nexusmarket.model.Inventory} record. */
public enum InventoryMovementType {
    INBOUND,
    RESERVATION,
    SALE_OUTBOUND,
    ADJUSTMENT,
    RETURN
}
