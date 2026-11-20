package application.domain.exceptions;

/** Invalid shipment data. */
public class InvalidShipmentException extends DomainValidationException {

    public InvalidShipmentException(String message) {
        super(message);
    }
}
