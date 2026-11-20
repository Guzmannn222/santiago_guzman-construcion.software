package application.domain.exceptions;

/** Invalid refund data. */
public class InvalidRefundException extends DomainValidationException {

    public InvalidRefundException(String message) {
        super(message);
    }
}
