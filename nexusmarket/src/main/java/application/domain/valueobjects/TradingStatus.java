package application.domain.valueobjects;

import java.util.List;

/**
 * Commercial status of a Buyer; only ACTIVE buyers can place orders.
 */
public final class TradingStatus extends DomainCatalog {

    public static final TradingStatus ACTIVE = new TradingStatus(
            "ACTIVE", "Active", "Buyer can place orders.");
    public static final TradingStatus PENDING_APPROVAL = new TradingStatus(
            "PENDING_APPROVAL", "Pending Approval", "Buyer awaits commercial approval.");
    public static final TradingStatus SUSPENDED = new TradingStatus(
            "SUSPENDED", "Suspended", "Buyer is temporarily not allowed to place orders.");
    public static final TradingStatus BLOCKED = new TradingStatus(
            "BLOCKED", "Blocked", "Buyer is permanently not allowed to place orders.");

    private static final List<TradingStatus> VALUES = List.of(ACTIVE, PENDING_APPROVAL, SUSPENDED, BLOCKED);

    private TradingStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<TradingStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static TradingStatus fromCode(String code) {
        return lookup("TradingStatus", code, VALUES);
    }
}
