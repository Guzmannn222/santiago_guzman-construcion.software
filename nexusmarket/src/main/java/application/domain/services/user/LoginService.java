package application.domain.services.user;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.ports.in.LoginUseCase;
import application.domain.services.support.AuthorizedService;

/** Public login: verifies credentials and issues a token. Failures never reveal which part was wrong. */
@Service
public class LoginService extends AuthorizedService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordServicePort passwordServicePort;
    private final JwtServicePort jwtServicePort;

    public LoginService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t, UserRepositoryPort userRepositoryPort,
            PasswordServicePort passwordServicePort, JwtServicePort jwtServicePort) {
        super(s, r, t);
        this.userRepositoryPort = userRepositoryPort;
        this.passwordServicePort = passwordServicePort;
        this.jwtServicePort = jwtServicePort;
    }

    @Override
    public AuthenticationResult login(User credentials) {
        if (credentials == null || credentials.getUsername() == null || credentials.getUsername().isBlank()
                || credentials.getPassword() == null || credentials.getPassword().isBlank()) {
            throw new InvalidCredentialsException();
        }
        User user = userRepositoryPort.findByUsername(credentials.getUsername().trim())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordServicePort.matches(credentials.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        validateStatus.execute(user);
        String token = jwtServicePort.generateToken(user);
        register(user, OperationType.USER_LOGIN, EntityType.USER, user.getId());
        return new AuthenticationResult(user, token);
    }
}
