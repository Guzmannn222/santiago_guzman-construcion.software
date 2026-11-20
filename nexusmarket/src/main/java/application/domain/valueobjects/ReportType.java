package application.domain.valueobjects;

import java.util.List;

/**
 * Kind of consolidated administrative report.
 */
public final class ReportType extends DomainCatalog {

    public static final ReportType SALES = new ReportType(
            "SALES", "Sales", "Orders and revenue summary.");
    public static final ReportType INVENTORY = new ReportType(
            "INVENTORY", "Inventory", "Stock levels summary by status.");
    public static final ReportType RETURNS_AND_REFUNDS = new ReportType(
            "RETURNS_AND_REFUNDS", "Returns and Refunds", "Returns and refunds summary.");

    private static final List<ReportType> VALUES = List.of(SALES, INVENTORY, RETURNS_AND_REFUNDS);

    private ReportType(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReportType> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static ReportType fromCode(String code) {
        return lookup("ReportType", code, VALUES);
    }
}
