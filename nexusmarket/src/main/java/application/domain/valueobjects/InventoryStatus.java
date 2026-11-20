package application.domain.valueobjects;

import java.util.List;

/**
 * Status of an Inventory record. DAMAGED stock can never be reserved for a sale.
 */
public final class InventoryStatus extends DomainCatalog {

    public static final InventoryStatus AVAILABLE = new InventoryStatus(
            "AVAILABLE", "Available", "Stock is above the low-stock threshold.");
    public static final InventoryStatus LOW_STOCK = new InventoryStatus(
            "LOW_STOCK", "Low Stock", "Stock is positive but at or below the low-stock threshold.");
    public static final InventoryStatus OUT_OF_STOCK = new InventoryStatus(
            "OUT_OF_STOCK", "Out of Stock", "There are no units available.");
    public static final InventoryStatus DAMAGED = new InventoryStatus(
            "DAMAGED", "Damaged", "Stock is marked as damaged and cannot be reserved.");

    private static final List<InventoryStatus> VALUES = List.of(AVAILABLE, LOW_STOCK, OUT_OF_STOCK, DAMAGED);

    private InventoryStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<InventoryStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static InventoryStatus fromCode(String code) {
        return lookup("InventoryStatus", code, VALUES);
    }
}
