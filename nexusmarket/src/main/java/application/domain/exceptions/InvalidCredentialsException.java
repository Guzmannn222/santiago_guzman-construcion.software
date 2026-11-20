package application.domain.exceptions;

/** Unknown username or wrong password; both cases return the same message (HTTP 401). */
public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Invalid username or password.");
    }
}
