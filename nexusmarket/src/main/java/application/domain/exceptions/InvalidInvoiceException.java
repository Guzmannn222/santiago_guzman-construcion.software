package application.domain.exceptions;

/** Invalid invoice data (e.g. tax rate out of range). */
public class InvalidInvoiceException extends DomainValidationException {

    public InvalidInvoiceException(String message) {
        super(message);
    }
}
