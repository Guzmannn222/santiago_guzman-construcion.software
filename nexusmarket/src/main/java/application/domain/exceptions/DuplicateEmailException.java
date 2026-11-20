package application.domain.exceptions;

/** A user with the same email already exists. */
public class DuplicateEmailException extends DomainException {

    public DuplicateEmailException(String message) {
        super(message);
    }
}
