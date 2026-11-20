package application.domain.exceptions;

/** The acting user is not allowed to execute the operation or to access the resource (RG-02/RG-03; HTTP 403). */
public class UnauthorizedOperationException extends DomainException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
