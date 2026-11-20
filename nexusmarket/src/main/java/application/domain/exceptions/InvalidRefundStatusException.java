package application.domain.exceptions;

/** Invalid RefundStatus transition or return not refundable. */
public class InvalidRefundStatusException extends DomainException {

    public InvalidRefundStatusException(String message) {
        super(message);
    }
}
