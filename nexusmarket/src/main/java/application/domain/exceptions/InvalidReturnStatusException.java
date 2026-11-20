package application.domain.exceptions;

/** Invalid ReturnStatus transition or order not eligible for return. */
public class InvalidReturnStatusException extends DomainException {

    public InvalidReturnStatusException(String message) {
        super(message);
    }
}
