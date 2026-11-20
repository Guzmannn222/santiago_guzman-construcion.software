package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.support.AuthorizedService;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.RegisterUserUseCase;

/** Administrator creates an account of any role. */
@Service
public class RegisterUserService extends AuthorizedService implements RegisterUserUseCase {

    private final CreateUserAccountService createUserAccountService;

    public RegisterUserService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, CreateUserAccountService createUserAccountService) {
        super(s, r, t);
        this.createUserAccountService = createUserAccountService;
    }

    @Override
    public User register(User actor, User newUser) {
        authorize(actor, SystemRole.ADMINISTRATOR);
        provided(newUser, "User data");
        provided(newUser.getRole(), "User role");
        User saved = createUserAccountService.execute(newUser, null);
        register(actor, OperationType.USER_REGISTRATION, EntityType.USER, saved.getId(),
                "username", saved.getUsername(), "role", saved.getRole().getCode());
        return saved;
    }
}
