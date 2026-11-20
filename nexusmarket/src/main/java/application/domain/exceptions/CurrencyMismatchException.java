package application.domain.exceptions;

/** Two money amounts in different currencies were mixed. */
public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}
