package application.domain.exceptions;

/** Invalid product or variant data. */
public class InvalidProductException extends DomainValidationException {

    public InvalidProductException(String message) {
        super(message);
    }
}
