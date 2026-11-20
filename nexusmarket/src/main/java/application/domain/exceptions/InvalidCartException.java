package application.domain.exceptions;

/** Invalid cart operation or data. */
public class InvalidCartException extends DomainValidationException {

    public InvalidCartException(String message) {
        super(message);
    }
}
