package application.domain.exceptions;

/** The resource already exists (e.g. a second invoice or shipment for the same order). */
public class DuplicateResourceException extends DomainException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
