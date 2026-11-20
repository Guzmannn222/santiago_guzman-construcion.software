package application.domain.exceptions;

/** Invalid operation record. */
public class InvalidOperationException extends DomainValidationException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
