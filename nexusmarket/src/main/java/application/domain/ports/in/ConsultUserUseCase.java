package application.domain.ports.in;

import application.domain.models.User;
import java.util.List;

/** Input Port: CU-01 consult users. A user can consult itself; ADMINISTRATOR can consult any user. */
public interface ConsultUserUseCase {

    User consultUser(User actor, User target);
    List<User> consultAllUsers(User actor);
}
