package application.domain.exceptions;

/** The buyer commercial status does not allow the operation. */
public class InvalidTradingStatusException extends DomainException {

    public InvalidTradingStatusException(String message) {
        super(message);
    }
}
