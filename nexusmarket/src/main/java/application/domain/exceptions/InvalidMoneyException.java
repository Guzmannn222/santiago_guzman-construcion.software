package application.domain.exceptions;

/** Invalid money amount or currency. */
public class InvalidMoneyException extends DomainValidationException {

    public InvalidMoneyException(String message) {
        super(message);
    }
}
