package application.domain.exceptions;

/** Invalid report request. */
public class InvalidReportException extends DomainValidationException {

    public InvalidReportException(String message) {
        super(message);
    }
}
