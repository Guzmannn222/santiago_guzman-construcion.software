package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.ChangeUserRoleUseCase;
import application.domain.services.support.AuthorizedService;

/** Administrator changes the role of another account; the old tokens are invalidated. */
@Service
public class ChangeUserRoleService extends AuthorizedService implements ChangeUserRoleUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ChangeUserRoleService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void changeRole(User actor, User target, SystemRole newRole) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(target, "Target user");
        provided(newRole, "New role");
        User user = found(userRepositoryPort.findById(idOf(target.getId(), "User")), "User", target.getId());
        if (user.isSameUserAs(actor)) {
            throw new UnauthorizedOperationException("An administrator cannot change their own role.");
        }
        SystemRole previous = user.getRole();
        user.changeRole(newRole);
        userRepositoryPort.update(user);
        register(actor, OperationType.USER_ROLE_CHANGE, EntityType.USER, user.getId(),
                "previousRole", previous == null ? null : previous.getCode(), "newRole", newRole.getCode());
    }
}
