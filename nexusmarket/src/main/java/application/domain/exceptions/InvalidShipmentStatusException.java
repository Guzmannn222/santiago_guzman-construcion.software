package application.domain.exceptions;

/** Invalid ShipmentStatus transition. */
public class InvalidShipmentStatusException extends DomainException {

    public InvalidShipmentStatusException(String message) {
        super(message);
    }
}
