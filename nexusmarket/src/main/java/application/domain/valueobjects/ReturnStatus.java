package application.domain.valueobjects;

import java.util.List;

/**
 * Status of a post-sale Return request.
 */
public final class ReturnStatus extends DomainCatalog {

    public static final ReturnStatus REQUESTED = new ReturnStatus(
            "REQUESTED", "Requested", "Return requested by the buyer.");
    public static final ReturnStatus APPROVED = new ReturnStatus(
            "APPROVED", "Approved", "Return approved by the seller.");
    public static final ReturnStatus REJECTED = new ReturnStatus(
            "REJECTED", "Rejected", "Return rejected by the seller.");
    public static final ReturnStatus COMPLETED = new ReturnStatus(
            "COMPLETED", "Completed", "Goods physically received back by logistics.");

    private static final List<ReturnStatus> VALUES = List.of(REQUESTED, APPROVED, REJECTED, COMPLETED);

    private ReturnStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ReturnStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static ReturnStatus fromCode(String code) {
        return lookup("ReturnStatus", code, VALUES);
    }
}
