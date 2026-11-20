package application.domain.ports.in;

import application.domain.models.User;

/** Input Port: change the password of the acting user (invalidates previous tokens). */
public interface ChangeUserPasswordUseCase {

    void changePassword(User actor, String currentPassword, String newPassword);
}
