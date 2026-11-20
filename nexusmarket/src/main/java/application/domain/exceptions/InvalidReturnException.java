package application.domain.exceptions;

/** Invalid return data. */
public class InvalidReturnException extends DomainValidationException {

    public InvalidReturnException(String message) {
        super(message);
    }
}
