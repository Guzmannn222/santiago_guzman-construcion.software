package application.adapters.useCases;

import application.domain.models.AuthenticationResult;
import application.domain.models.Buyer;
import application.domain.models.User;
import application.domain.ports.in.LoginUseCase;
import application.domain.ports.in.LogoutUseCase;
import application.domain.ports.in.PublicAccessPort;
import application.domain.ports.in.RegisterBuyerAccountUseCase;
import org.springframework.stereotype.Service;

/** Facade for the actions available without an authenticated session. */
@Service
public class PublicAccessUseCaseImpl implements PublicAccessPort {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RegisterBuyerAccountUseCase registerBuyerAccountUseCase;

    public PublicAccessUseCaseImpl(LoginUseCase loginUseCase, LogoutUseCase logoutUseCase,
            RegisterBuyerAccountUseCase registerBuyerAccountUseCase) {
        this.loginUseCase = loginUseCase;
        this.logoutUseCase = logoutUseCase;
        this.registerBuyerAccountUseCase = registerBuyerAccountUseCase;
    }

    @Override
    public AuthenticationResult login(User credentials) {
        return loginUseCase.login(credentials);
    }

    @Override
    public void logout(User authenticatedUser) {
        // Anonymous calls are an idempotent no-op: the client simply discards its token.
        if (authenticatedUser == null || authenticatedUser.getId() == null) {
            return;
        }
        logoutUseCase.logout(authenticatedUser);
    }

    @Override
    public Buyer registerBuyer(User newUser, String primaryAddress) {
        return registerBuyerAccountUseCase.registerBuyerAccount(newUser, primaryAddress);
    }
}
