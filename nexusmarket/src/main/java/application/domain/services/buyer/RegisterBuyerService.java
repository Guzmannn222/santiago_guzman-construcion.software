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
import application.domain.ports.in.RegisterBuyerUseCase;

/** An existing BUYER user creates their commercial profile (once) and cart. */
@Service
public class RegisterBuyerService extends AuthorizedService implements RegisterBuyerUseCase {

    private final BuyerRepositoryPort buyerRepositoryPort;
    private final CartRepositoryPort cartRepositoryPort;

    public RegisterBuyerService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            BuyerRepositoryPort buyerRepositoryPort, CartRepositoryPort cartRepositoryPort) {
        super(s, r, t);
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.cartRepositoryPort = cartRepositoryPort;
    }

    @Override
    public Buyer registerBuyer(User actor, String primaryAddress) {
        authorize(actor, SystemRole.BUYER);
        if (buyerRepositoryPort.findByUserId(actor.getId()).isPresent()) {
            throw new DuplicateResourceException("The user already has a buyer profile.");
        }
        Buyer buyer = buyerRepositoryPort.save(new Buyer(actor, primaryAddress));
        buyer.setCart(cartRepositoryPort.save(new Cart(buyer)));
        register(actor, OperationType.BUYER_REGISTRATION, EntityType.BUYER, buyer.getId());
        return buyer;
    }
}
