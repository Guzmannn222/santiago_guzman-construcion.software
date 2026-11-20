package application.domain.ports.in;

import application.domain.models.User;

/** Input Port: invalidate every token issued to the user. */
public interface LogoutUseCase {

    void logout(User actor);
}
