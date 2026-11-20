package application.domain.exceptions;

/** The cart has no products, so no order can be created from it. */
public class EmptyCartException extends DomainException {

    public EmptyCartException(String message) {
        super(message);
    }
}
