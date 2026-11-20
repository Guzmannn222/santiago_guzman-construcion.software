package application.domain.ports.in;

import application.domain.models.User;

/** Input Port: CU-01 register a user (role ADMINISTRATOR). The new user carries username, plain password, name, email, identity document and role. */
public interface RegisterUserUseCase {

    User register(User actor, User newUser);
}
