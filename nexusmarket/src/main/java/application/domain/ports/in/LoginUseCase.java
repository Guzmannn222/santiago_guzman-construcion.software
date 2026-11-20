package application.domain.ports.in;

import application.domain.models.AuthenticationResult;
import application.domain.models.User;

/** Input Port: authenticate a user and issue a token (unknown user and wrong password are indistinguishable). */
public interface LoginUseCase {

    AuthenticationResult login(User credentials);
}
