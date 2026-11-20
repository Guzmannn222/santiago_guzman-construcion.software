package application.domain.exceptions;

/** Invalid UserStatus transition. */
public class InvalidUserStatusException extends DomainException {

    public InvalidUserStatusException(String message) {
        super(message);
    }
}
