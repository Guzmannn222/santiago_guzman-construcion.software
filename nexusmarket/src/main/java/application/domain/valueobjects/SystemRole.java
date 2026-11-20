package application.domain.valueobjects;

import java.util.List;

/**
 * Business role of a platform participant. Each user has exactly one role (RG-02).
 */
public final class SystemRole extends DomainCatalog {

    public static final SystemRole BUYER = new SystemRole(
            "BUYER", "Buyer", "Customer that browses the catalog, manages a cart and places orders.");
    public static final SystemRole SELLER = new SystemRole(
            "SELLER", "Seller", "Provider that publishes products and manages its own inventory.");
    public static final SystemRole LOGISTICS_OPERATOR = new SystemRole(
            "LOGISTICS_OPERATOR", "Logistics Operator", "Operator that manages warehouses, stock movements, shipments and deliveries.");
    public static final SystemRole ADMINISTRATOR = new SystemRole(
            "ADMINISTRATOR", "Administrator", "Administrator of users, sellers, warehouses, invoices and refunds.");
    public static final SystemRole SUPERVISOR = new SystemRole(
            "SUPERVISOR", "Supervisor", "Read-only profile for monitoring reports and the audit trail.");

    private static final List<SystemRole> VALUES = List.of(BUYER, SELLER, LOGISTICS_OPERATOR, ADMINISTRATOR, SUPERVISOR);

    private SystemRole(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<SystemRole> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static SystemRole fromCode(String code) {
        return lookup("SystemRole", code, VALUES);
    }
}
