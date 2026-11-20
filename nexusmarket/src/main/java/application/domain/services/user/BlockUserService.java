package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.BlockUserUseCase;
import application.domain.services.support.AuthorizedService;

/** Administrator blocks an account (RG-04: an administrator cannot block themselves). */
@Service
public class BlockUserService extends AuthorizedService implements BlockUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public BlockUserService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void block(User actor, User target) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(target, "Target user");
        User user = found(userRepositoryPort.findById(idOf(target.getId(), "User")), "User", target.getId());
        if (user.isSameUserAs(actor)) {
            throw new UnauthorizedOperationException("An administrator cannot block their own account.");
        }
        user.block();
        userRepositoryPort.update(user);
        register(actor, OperationType.USER_BLOCKING, EntityType.USER, user.getId(), "username", user.getUsername());
    }
}
