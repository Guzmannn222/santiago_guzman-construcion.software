package application.domain.exceptions;

/** Base class of every NexusMarket domain exception (business-rule violation, mapped to HTTP 409). */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
