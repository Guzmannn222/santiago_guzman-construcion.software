package application.domain.exceptions;

/** The order state does not allow the operation (a FINISHED order can never be modified). */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
