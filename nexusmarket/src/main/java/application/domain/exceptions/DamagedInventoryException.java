package application.domain.exceptions;

/** Inventory marked as DAMAGED can never be reserved or sold. */
public class DamagedInventoryException extends DomainException {

    public DamagedInventoryException(String message) {
        super(message);
    }
}
