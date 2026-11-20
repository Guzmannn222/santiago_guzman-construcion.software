package application.domain.exceptions;

/** Base class of exceptions raised when the data supplied to the domain is invalid (mapped to HTTP 400). */
public class DomainValidationException extends DomainException {

    public DomainValidationException(String message) {
        super(message);
    }
}
