package application.domain.valueobjects;

import java.util.List;

/**
 * Logistics status of a Shipment.
 */
public final class ShipmentStatus extends DomainCatalog {

    public static final ShipmentStatus PENDING = new ShipmentStatus(
            "PENDING", "Pending", "Shipment created and not yet prepared.");
    public static final ShipmentStatus PREPARING = new ShipmentStatus(
            "PREPARING", "Preparing", "Shipment is being packed.");
    public static final ShipmentStatus DISPATCHED = new ShipmentStatus(
            "DISPATCHED", "Dispatched", "Shipment left the warehouse.");
    public static final ShipmentStatus IN_TRANSIT = new ShipmentStatus(
            "IN_TRANSIT", "In Transit", "Shipment is on its way to the buyer.");
    public static final ShipmentStatus DELIVERED = new ShipmentStatus(
            "DELIVERED", "Delivered", "Shipment delivered to the buyer.");

    private static final List<ShipmentStatus> VALUES = List.of(PENDING, PREPARING, DISPATCHED, IN_TRANSIT, DELIVERED);

    private ShipmentStatus(String code, String name, String description) {
        super(code, name, description);
    }

    public static List<ShipmentStatus> values() {
        return VALUES;
    }

    /**
     * Resolves a catalog entry from its code (case-sensitive).
     *
     * @return the catalog entry, or {@code null} when {@code code} is {@code null}
     * @throws IllegalArgumentException when the code is not part of the catalog
     */
    public static ShipmentStatus fromCode(String code) {
        return lookup("ShipmentStatus", code, VALUES);
    }
}
