package application.domain.exceptions;

/** A user with the same identity document already exists. */
public class DuplicateIdentityDocumentException extends DomainException {

    public DuplicateIdentityDocumentException(String message) {
        super(message);
    }
}
