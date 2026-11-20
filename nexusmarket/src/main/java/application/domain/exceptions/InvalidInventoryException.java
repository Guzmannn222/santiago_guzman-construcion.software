package application.domain.exceptions;

/** Invalid inventory data. */
public class InvalidInventoryException extends DomainValidationException {

    public InvalidInventoryException(String message) {
        super(message);
    }
}
