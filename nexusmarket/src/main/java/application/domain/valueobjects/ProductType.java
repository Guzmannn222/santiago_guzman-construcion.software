package application.domain.valueobjects;

import java.util.List;

/**
 * Distinguishes physical goods (inventory + shipping) from digital goods (delivered right after payment).
 */
public final class ProductType extends DomainCatalog {

    public static final ProductType PHYSICAL = new ProductType(
            "PHYSICAL", "Physical", "Requires inventory and shipping.");
    public static final ProductType DIGITAL = new ProductType(
            "DIGITAL", "Digital", "Delivered immediately after payment; no inventory or shipping.");

    private static final List<ProductType> VALUES = List.of(PHYSICAL, DIGITAL);

    private ProductType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static ProductType fromCode(String code) {
        return lookup("ProductType", code, VALUES);
    }
}
