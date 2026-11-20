package application.domain.ports.in;

import application.domain.models.User;

/** Input Port: CU-01 block a user (role ADMINISTRATOR). */
public interface BlockUserUseCase {

    void block(User actor, User target);
}
