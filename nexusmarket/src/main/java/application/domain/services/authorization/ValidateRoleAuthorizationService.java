package application.domain.services.authorization;

import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.valueobjects.SystemRole;
import java.util.Arrays;
import org.springframework.stereotype.Service;

/** RG-02/RG-03: each service is restricted to the role(s) the business specification assigns. */
@Service
public class ValidateRoleAuthorizationService {

    public void execute(User actor, SystemRole... allowedRoles) {
        if (actor == null) {
            throw new UnauthorizedOperationException("The acting user must be provided.");
        }
        boolean allowed = actor.getRole() != null && Arrays.asList(allowedRoles).contains(actor.getRole());
        if (!allowed) {
            throw new UnauthorizedOperationException("User " + actor.getUsername() + " with role "
                    + (actor.getRole() == null ? "UNDEFINED" : actor.getRole().getCode())
                    + " is not authorized to execute this operation. Allowed roles: "
                    + Arrays.toString(allowedRoles));
        }
    }
}
