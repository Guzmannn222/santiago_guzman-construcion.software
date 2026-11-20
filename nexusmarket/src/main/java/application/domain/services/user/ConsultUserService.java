package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.ConsultUserUseCase;
import application.domain.services.support.AuthorizedService;

/** Administrator consults any account; any user may consult their own. */
@Service
public class ConsultUserService extends AuthorizedService implements ConsultUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ConsultUserService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User consultUser(User actor, User target) {
        validateStatus.execute(actor);
        provided(target, "Target user");
        User user = found(userRepositoryPort.findById(idOf(target.getId(), "User")), "User", target.getId());
        if (!user.isSameUserAs(actor)) {
            validateRole.execute(actor, SystemRole.ADMINISTRATOR);
        }
        return user;
    }

    @Override
    public List<User> consultAllUsers(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        return userRepositoryPort.findAll();
    }
}
