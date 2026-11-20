package application.domain.exceptions;

/** The invoice state does not allow the operation. */
public class InvalidInvoiceStatusException extends DomainException {

    public InvalidInvoiceStatusException(String message) {
        super(message);
    }
}
