package application.domain.exceptions;

/** Invalid price (must be strictly positive). */
public class InvalidPriceException extends DomainValidationException {

    public InvalidPriceException(String message) {
        super(message);
    }
}
