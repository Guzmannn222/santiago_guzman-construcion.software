package application.domain.services.cart;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultCartUseCase;

/** Buyers see only their own cart; administrators may inspect any cart. */
@Service
public class ConsultCartService extends AuthorizedService implements ConsultCartUseCase {

    private final CartRepositoryPort cartRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;

    public ConsultCartService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            CartRepositoryPort cartRepositoryPort, BuyerRepositoryPort buyerRepositoryPort) {
        super(s, r, t);
        this.cartRepositoryPort = cartRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
    }

    @Override
    public Cart consultCart(User actor, Cart cart) {
        authorize(actor, SystemRole.BUYER, SystemRole.ADMINISTRATOR);
        provided(cart, "Cart");
        Cart persisted = found(cartRepositoryPort.findById(idOf(cart.getId(), "Cart")), "Cart", cart.getId());
        if (SystemRole.BUYER.equals(actor.getRole()) && !persisted.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult their own cart.");
        }
        return persisted;
    }

    @Override
    public Cart consultMyCart(User actor) {
        authorize(actor, SystemRole.BUYER);
        Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
        return found(cartRepositoryPort.findByBuyerId(buyer.getId()), "Cart", buyer.getId());
    }
}
