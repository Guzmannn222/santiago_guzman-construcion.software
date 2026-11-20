package application.domain.exceptions;

/** The seller status does not allow the operation. */
public class InvalidSellerStatusException extends DomainException {

    public InvalidSellerStatusException(String message) {
        super(message);
    }
}
