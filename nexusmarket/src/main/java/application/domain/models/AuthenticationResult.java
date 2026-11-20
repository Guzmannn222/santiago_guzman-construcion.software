package application.domain.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Outcome of a successful authentication: the authenticated user and the issued token. */
@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResult {
    private User authenticatedUser;
    private String token;

    public AuthenticationResult(User authenticatedUser, String token) {
        this.authenticatedUser = authenticatedUser;
        this.token = token;
    }
}
