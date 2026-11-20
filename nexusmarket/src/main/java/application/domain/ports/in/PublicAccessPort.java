package application.domain.ports.in;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.User;

/** Public (unauthenticated) operations: login, logout and buyer self-registration. */
public interface PublicAccessPort {

    AuthenticationResult login(User credentials);
    void logout(User authenticatedUser);
    Buyer registerBuyer(User newUser, String primaryAddress);
}
