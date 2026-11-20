package application.domain.valueobjects;

import java.util.List;

/**
 * Distinguishes marketplace-owned warehouses from seller-owned warehouses.
 */
public final class WarehouseType extends DomainCatalog {

    public static final WarehouseType MARKETPLACE = new WarehouseType(
            "MARKETPLACE", "Marketplace", "Warehouse owned by the marketplace.");
    public static final WarehouseType SELLER = new WarehouseType(
            "SELLER", "Seller", "Warehouse owned by a seller.");

    private static final List<WarehouseType> VALUES = List.of(MARKETPLACE, SELLER);

    private WarehouseType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<WarehouseType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static WarehouseType fromCode(String code) {
        return lookup("WarehouseType", code, VALUES);
    }
}
