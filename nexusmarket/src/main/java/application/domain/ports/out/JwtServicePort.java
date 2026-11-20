package application.domain.ports.out;

import application.domain.models.User;

/** Output port: token issuing and validation. The domain never depends on a JWT library. */
public interface JwtServicePort {

    /** Issues a signed token carrying the user identity ({@code sub}), token version ({@code ver}) and the snapshot needed to rebuild the User. */
    String generateToken(User user);
    boolean validateToken(String token);
    /** Internal user id ({@code sub} claim); {@code null} if absent or unparseable. */
    Long extractUserId(String token);
    /** Token version ({@code ver} claim); {@code null} if absent or unparseable. */
    Integer extractTokenVersion(String token);
    /** Rebuilds the User (id, username, role, status, version...) from the token claims. */
    User reconstructUser(String token);
}
