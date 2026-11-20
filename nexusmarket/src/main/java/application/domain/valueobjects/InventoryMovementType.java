package application.domain.valueobjects;

import java.util.List;

/**
 * Types of movement recorded against an Inventory record.
 */
public final class InventoryMovementType extends DomainCatalog {

    public static final InventoryMovementType INBOUND = new InventoryMovementType(
            "INBOUND", "Inbound", "Units received into the warehouse.");
    public static final InventoryMovementType RESERVATION = new InventoryMovementType(
            "RESERVATION", "Reservation", "Units reserved for a pending order.");
    public static final InventoryMovementType SALE_OUTBOUND = new InventoryMovementType(
            "SALE_OUTBOUND", "Sale Outbound", "Units leaving the warehouse because of a sale.");
    public static final InventoryMovementType ADJUSTMENT = new InventoryMovementType(
            "ADJUSTMENT", "Adjustment", "Absolute stock correction after a physical count.");
    public static final InventoryMovementType RETURN = new InventoryMovementType(
            "RETURN", "Return", "Units returned to the warehouse (cancelled order or product return).");

    private static final List<InventoryMovementType> VALUES = List.of(INBOUND, RESERVATION, SALE_OUTBOUND, ADJUSTMENT, RETURN);

    private InventoryMovementType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<InventoryMovementType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static InventoryMovementType fromCode(String code) {
        return lookup("InventoryMovementType", code, VALUES);
    }
}
