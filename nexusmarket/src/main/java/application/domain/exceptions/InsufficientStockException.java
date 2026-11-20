package application.domain.exceptions;

/** Not enough stock to satisfy the operation (stock can never go negative). */
public class InsufficientStockException extends DomainException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
