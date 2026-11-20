package application.domain.services.user;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.User;
import application.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

/**
 * Loads the authoritative persisted user referenced by the {@code sub} claim of a token. Pure
 * domain: it knows nothing about Spring Security; the security adapter wraps the result.
 */
@Service
public class LoadAuthenticatedUserService {

    private final UserRepositoryPort userRepositoryPort;

    public LoadAuthenticatedUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    public User loadById(Long userId) {
        if (userId == null) {
            throw new UnauthorizedOperationException("Authenticated user id must be provided.");
        }
        return userRepositoryPort.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", userId));
    }
}
