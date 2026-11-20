package application.domain.models;

import application.domain.exceptions.InvalidShipmentException;
import application.domain.exceptions.InvalidShipmentStatusException;
import application.domain.valueobjects.ShipmentStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Physical logistics of an {@link Order}. Transitions: PENDING -> PREPARING -> DISPATCHED ->
 * IN_TRANSIT -> DELIVERED (IN_TRANSIT is optional).
 */
@Getter
@Setter
@NoArgsConstructor
public class Shipment {

    private Long id;
    private Order order;
    private String address;
    private ShipmentStatus status = ShipmentStatus.PENDING;
    private LocalDateTime dispatchDate;
    private LocalDateTime deliveryDate;

    public Shipment(Order order, String address) {
        if (order == null) {
            throw new InvalidShipmentException("Shipment order must be provided.");
        }
        if (!order.hasPhysicalItems()) {
            throw new InvalidShipmentException("Orders made only of digital products have no shipment.");
        }
        if (address == null || address.isBlank() || address.length() > 200) {
            throw new InvalidShipmentException("Shipment address must be provided (max 200 characters).");
        }
        this.order = order;
        this.address = address.trim();
        this.status = ShipmentStatus.PENDING;
    }

    public void prepare() {
        requireStatus(ShipmentStatus.PENDING, "prepared");
        this.status = ShipmentStatus.PREPARING;
    }

    public void dispatch() {
        requireStatus(ShipmentStatus.PREPARING, "dispatched");
        this.order.ship();
        this.status = ShipmentStatus.DISPATCHED;
        this.dispatchDate = LocalDateTime.now();
    }

    /** Only tracking moves are allowed here (DISPATCHED -> IN_TRANSIT); delivery uses confirmDelivery. */
    public void updateStatus(ShipmentStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidShipmentStatusException("Target shipment status must be provided.");
        }
        if (!(ShipmentStatus.DISPATCHED.equals(status) && ShipmentStatus.IN_TRANSIT.equals(newStatus))) {
            throw new InvalidShipmentStatusException("Invalid shipment status transition from " + status.getCode()
                    + " to " + newStatus.getCode() + ".");
        }
        this.status = newStatus;
    }

    public void confirmDelivery() {
        if (!ShipmentStatus.DISPATCHED.equals(status) && !ShipmentStatus.IN_TRANSIT.equals(status)) {
            throw new InvalidShipmentStatusException(
                    "A shipment in status " + status.getCode() + " cannot be delivered.");
        }
        this.order.deliver();
        this.order.finish();
        this.status = ShipmentStatus.DELIVERED;
        this.deliveryDate = LocalDateTime.now();
    }

    private void requireStatus(ShipmentStatus expected, String action) {
        if (!expected.equals(status)) {
            throw new InvalidShipmentStatusException("A shipment in status " + status.getCode()
                    + " cannot be " + action + " (expected " + expected.getCode() + ").");
        }
    }
}
