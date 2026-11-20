package application.domain.exceptions;

/** Invalid ProductStatus transition or product not purchasable. */
public class InvalidProductStatusException extends DomainException {

    public InvalidProductStatusException(String message) {
        super(message);
    }
}
