package application.domain.services.buyer;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.RegisterBuyerAccountUseCase;
import application.domain.services.user.CreateUserAccountService;

/** Public self-registration: creates a BUYER account, its commercial profile and its empty cart. */
@Service
public class RegisterBuyerAccountService extends AuthorizedService implements RegisterBuyerAccountUseCase {

    private final CreateUserAccountService createUserAccountService;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final CartRepositoryPort cartRepositoryPort;

    public RegisterBuyerAccountService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            CreateUserAccountService createUserAccountService, BuyerRepositoryPort buyerRepositoryPort,
            CartRepositoryPort cartRepositoryPort) {
        super(s, r, t);
        this.createUserAccountService = createUserAccountService;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.cartRepositoryPort = cartRepositoryPort;
    }

    @Override
    public Buyer registerBuyerAccount(User newUser, String primaryAddress) {
        provided(newUser, "User data");
        Buyer draft = new Buyer(newUser, primaryAddress); // validates the address before touching storage
        User user = createUserAccountService.execute(newUser, SystemRole.BUYER);
        draft.setUser(user);
        Buyer buyer = buyerRepositoryPort.save(draft);
        Cart cart = cartRepositoryPort.save(new Cart(buyer));
        buyer.setCart(cart);
        register(user, OperationType.USER_REGISTRATION, EntityType.USER, user.getId(), "username", user.getUsername(),
                "role", SystemRole.BUYER.getCode());
        register(user, OperationType.BUYER_REGISTRATION, EntityType.BUYER, buyer.getId());
        return buyer;
    }
}
