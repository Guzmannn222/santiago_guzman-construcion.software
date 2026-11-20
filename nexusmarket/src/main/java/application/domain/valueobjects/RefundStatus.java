package application.domain.valueobjects;

import java.util.List;

/**
 * Status of a Refund.
 */
public final class RefundStatus extends DomainCatalog {

    public static final RefundStatus PENDING = new RefundStatus(
            "PENDING", "Pending", "Refund issued and waiting to be processed.");
    public static final RefundStatus PROCESSING = new RefundStatus(
            "PROCESSING", "Processing", "Refund payment in progress.");
    public static final RefundStatus COMPLETED = new RefundStatus(
            "COMPLETED", "Completed", "Money reimbursed to the buyer.");
    public static final RefundStatus REJECTED = new RefundStatus(
            "REJECTED", "Rejected", "Refund rejected.");

    private static final List<RefundStatus> VALUES = List.of(PENDING, PROCESSING, COMPLETED, REJECTED);

    private RefundStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<RefundStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static RefundStatus fromCode(String code) {
        return lookup("RefundStatus", code, VALUES);
    }
}
