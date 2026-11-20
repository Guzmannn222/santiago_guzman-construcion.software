package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.LogoutUseCase;
import application.domain.services.support.AuthorizedService;

/** Logout invalidates every token issued so far by bumping the token version. */
@Service
public class LogoutService extends AuthorizedService implements LogoutUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public LogoutService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void logout(User actor) {
        validateStatus.execute(actor);
        User user = found(userRepositoryPort.findById(idOf(actor.getId(), "User")), "User", actor.getId());
        user.bumpAuthTokenVersion();
        userRepositoryPort.update(user);
        register(user, OperationType.USER_LOGOUT, EntityType.USER, user.getId());
    }
}
