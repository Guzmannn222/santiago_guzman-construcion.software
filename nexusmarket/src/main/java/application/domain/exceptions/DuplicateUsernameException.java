package application.domain.exceptions;

/** A user with the same username already exists. */
public class DuplicateUsernameException extends DomainException {

    public DuplicateUsernameException(String message) {
        super(message);
    }
}
