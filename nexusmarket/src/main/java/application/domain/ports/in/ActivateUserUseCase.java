package application.domain.ports.in;

import application.domain.models.User;

/** Input Port: CU-01 activate a user (role ADMINISTRATOR). */
public interface ActivateUserUseCase {

    void activate(User actor, User target);
}
