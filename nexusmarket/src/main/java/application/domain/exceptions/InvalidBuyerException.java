package application.domain.exceptions;

/** Invalid buyer data. */
public class InvalidBuyerException extends DomainValidationException {

    public InvalidBuyerException(String message) {
        super(message);
    }
}
