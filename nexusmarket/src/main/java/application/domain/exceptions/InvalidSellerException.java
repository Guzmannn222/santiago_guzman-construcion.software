package application.domain.exceptions;

/** Invalid seller data. */
public class InvalidSellerException extends DomainValidationException {

    public InvalidSellerException(String message) {
        super(message);
    }
}
