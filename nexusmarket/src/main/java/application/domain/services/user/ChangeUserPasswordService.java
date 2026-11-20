package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.ChangeUserPasswordUseCase;
import application.domain.services.support.AuthorizedService;

/** Any active user changes their own password; previous tokens stop being valid. */
@Service
public class ChangeUserPasswordService extends AuthorizedService implements ChangeUserPasswordUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public ChangeUserPasswordService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
    }

    @Override
    public void changePassword(User actor, String currentPassword, String newPassword) {
        validateStatus.execute(actor);
        provided(currentPassword, "Current password");
        provided(newPassword, "New password");
        User user = found(userRepositoryPort.findById(idOf(actor.getId(), "User")), "User", actor.getId());
        if (!passwordServicePort.matches(currentPassword, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        User.validatePasswordFormat(newPassword);
        if (currentPassword.equals(newPassword)) {
            throw new InvalidUserException("The new password must be different from the current one.");
        }
        user.setPassword(passwordServicePort.encrypt(newPassword));
        user.bumpAuthTokenVersion();
        userRepositoryPort.update(user);
        register(user, OperationType.USER_PASSWORD_CHANGE, EntityType.USER, user.getId());
    }
}
