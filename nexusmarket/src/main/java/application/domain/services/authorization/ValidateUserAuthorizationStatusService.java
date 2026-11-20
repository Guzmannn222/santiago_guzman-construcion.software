package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import org.springframework.stereotype.Service;

/** RG-01: every operation must be executed by an authenticated, ACTIVE user. */
@Service
public class ValidateUserAuthorizationStatusService {

    public void execute(User actor) {
        if (actor == null) {
            throw new UnauthorizedOperationException("The acting user must be provided.");
        }
        if (!actor.validateAccess()) {
            throw new UnauthorizedOperationException(
                    "User " + actor.getUsername() + " is not active and cannot perform operations.");
        }
    }
}
