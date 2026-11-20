package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.ActivateUserUseCase;
import application.domain.services.support.AuthorizedService;

/** Administrator re-activates a blocked or inactive account. */
@Service
public class ActivateUserService extends AuthorizedService implements ActivateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ActivateUserService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void activate(User actor, User target) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(target, "Target user");
        User user = found(userRepositoryPort.findById(idOf(target.getId(), "User")), "User", target.getId());

        user.activate();
        userRepositoryPort.update(user);
        register(actor, OperationType.USER_ACTIVATION, EntityType.USER, user.getId(), "username", user.getUsername());
    }
}
