package application.domain.exceptions;

/** Invalid quantity (stock can never be negative). */
public class InvalidQuantityException extends DomainValidationException {

    public InvalidQuantityException(String message) {
        super(message);
    }
}
