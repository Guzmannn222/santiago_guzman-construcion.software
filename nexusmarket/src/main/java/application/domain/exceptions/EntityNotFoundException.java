package application.domain.exceptions;

/** A referenced entity does not exist (HTTP 404). */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String entity) {
        super(entity + " not found.");
    }

    public EntityNotFoundException(String entity, Object identifier) {
        super(entity + " not found" + (identifier == null ? "." : " with identifier '" + identifier + "'."));
    }
}
