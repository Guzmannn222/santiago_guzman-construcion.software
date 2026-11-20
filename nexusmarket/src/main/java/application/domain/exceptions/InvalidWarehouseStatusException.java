package application.domain.exceptions;

/** The warehouse status does not allow the operation. */
public class InvalidWarehouseStatusException extends DomainException {

    public InvalidWarehouseStatusException(String message) {
        super(message);
    }
}
