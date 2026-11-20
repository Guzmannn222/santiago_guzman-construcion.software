package application.domain.exceptions;

/** Invalid or incomplete user data. */
public class InvalidUserException extends DomainValidationException {

    public InvalidUserException(String message) {
        super(message);
    }
}
