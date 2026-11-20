package application.domain.exceptions;

/** Invalid order data. */
public class InvalidOrderException extends DomainValidationException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
