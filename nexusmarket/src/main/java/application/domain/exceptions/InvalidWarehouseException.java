package application.domain.exceptions;

/** Invalid warehouse data. */
public class InvalidWarehouseException extends DomainValidationException {

    public InvalidWarehouseException(String message) {
        super(message);
    }
}
