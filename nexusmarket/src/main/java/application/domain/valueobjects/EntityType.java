package application.domain.valueobjects;

import java.util.List;

/**
 * Kind of domain entity referenced by an Operation or AuditLog.
 */
public final class EntityType extends DomainCatalog {

    public static final EntityType USER = new EntityType(
            "USER", "User", "Platform user.");
    public static final EntityType BUYER = new EntityType(
            "BUYER", "Buyer", "Buyer profile.");
    public static final EntityType SELLER = new EntityType(
            "SELLER", "Seller", "Seller profile.");
    public static final EntityType WAREHOUSE = new EntityType(
            "WAREHOUSE", "Warehouse", "Warehouse.");
    public static final EntityType PRODUCT = new EntityType(
            "PRODUCT", "Product", "Catalog product.");
    public static final EntityType INVENTORY = new EntityType(
            "INVENTORY", "Inventory", "Inventory record (product in a warehouse).");
    public static final EntityType CART = new EntityType(
            "CART", "Cart", "Shopping cart.");
    public static final EntityType ORDER = new EntityType(
            "ORDER", "Order", "Order.");
    public static final EntityType INVOICE = new EntityType(
            "INVOICE", "Invoice", "Invoice.");
    public static final EntityType SHIPMENT = new EntityType(
            "SHIPMENT", "Shipment", "Shipment.");
    public static final EntityType RETURN = new EntityType(
            "RETURN", "Return", "Return request.");
    public static final EntityType REFUND = new EntityType(
            "REFUND", "Refund", "Refund.");
    public static final EntityType REPORT = new EntityType(
            "REPORT", "Report", "Administrative report.");

    private static final List<EntityType> VALUES = List.of(USER, BUYER, SELLER, WAREHOUSE, PRODUCT, INVENTORY, CART, ORDER, INVOICE, SHIPMENT, RETURN, REFUND, REPORT);

    private EntityType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<EntityType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static EntityType fromCode(String code) {
        return lookup("EntityType", code, VALUES);
    }
}
