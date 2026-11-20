package application.domain.valueobjects;

import java.util.List;

/**
 * Publication status of a Product in the catalog.
 */
public final class ProductStatus extends DomainCatalog {

    public static final ProductStatus PUBLISHED = new ProductStatus(
            "PUBLISHED", "Published", "Visible in the catalog and purchasable.");
    public static final ProductStatus SUSPENDED = new ProductStatus(
            "SUSPENDED", "Suspended", "Not visible in the catalog; can be published again.");
    public static final ProductStatus DISCONTINUED = new ProductStatus(
            "DISCONTINUED", "Discontinued", "Permanently withdrawn from the catalog.");

    private static final List<ProductStatus> VALUES = List.of(PUBLISHED, SUSPENDED, DISCONTINUED);

    private ProductStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ProductStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static ProductStatus fromCode(String code) {
        return lookup("ProductStatus", code, VALUES);
    }
}
