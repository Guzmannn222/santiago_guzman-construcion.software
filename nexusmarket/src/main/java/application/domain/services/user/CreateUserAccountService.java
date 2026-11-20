package application.domain.services.user;

import application.domain.exceptions.DuplicateEmailException;
import application.domain.exceptions.DuplicateIdentityDocumentException;
import application.domain.exceptions.DuplicateUsernameException;
import application.domain.exceptions.InvalidUserException;
import application.domain.models.User;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal domain service: validates, de-duplicates, encrypts and persists a new account. */
@Service
public class CreateUserAccountService {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public CreateUserAccountService(UserRepositoryPort userRepositoryPort, PasswordServicePort passwordServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
    }

    /** @param forcedRole when not null it overrides the role requested in {@code newUser}. */
    public User execute(User newUser, SystemRole forcedRole) {
        if (newUser == null) {
            throw new InvalidUserException("User data must be provided.");
        }
        if (forcedRole != null) {
            newUser.setRole(forcedRole);
        }
        newUser.ensureRegistrationDataComplete();
        User.validatePasswordFormat(newUser.getPassword());
        if (userRepositoryPort.findByUsername(newUser.getUsername()).isPresent()) {
            throw new DuplicateUsernameException("The username '" + newUser.getUsername() + "' is already registered.");
        }
        if (userRepositoryPort.findByEmail(newUser.getEmail()).isPresent()) {
            throw new DuplicateEmailException("The email '" + newUser.getEmail() + "' is already registered.");
        }
        if (userRepositoryPort.findByIdentityDocument(newUser.getIdentityDocument()).isPresent()) {
            throw new DuplicateIdentityDocumentException(
                    "The identity document '" + newUser.getIdentityDocument() + "' is already registered.");
        }
        newUser.setPassword(passwordServicePort.encrypt(newUser.getPassword()));
        newUser.setStatus(application.domain.valueobjects.UserStatus.ACTIVE);
        newUser.setAuthTokenVersion(1);
        return userRepositoryPort.save(newUser);
    }
}
