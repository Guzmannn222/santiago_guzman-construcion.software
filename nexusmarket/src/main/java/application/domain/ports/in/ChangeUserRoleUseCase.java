package application.domain.ports.in;

import application.domain.models.User;
import application.domain.valueobjects.SystemRole;

/** Input Port: CU-01 change the role of a user (role ADMINISTRATOR). */
public interface ChangeUserRoleUseCase {

    void changeRole(User actor, User target, SystemRole newRole);
}
