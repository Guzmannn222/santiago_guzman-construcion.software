package application.domain.exceptions;

/** Invalid audit log record. */
public class InvalidAuditLogException extends DomainValidationException {

    public InvalidAuditLogException(String message) {
        super(message);
    }
}
