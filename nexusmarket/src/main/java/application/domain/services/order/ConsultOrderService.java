package application.domain.services.order;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ConsultOrderUseCase;

/** Buyers: own orders; sellers: orders containing their products; operational roles: all. */
@Service
public class ConsultOrderService extends AuthorizedService implements ConsultOrderUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultOrderService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            OrderRepositoryPort orderRepositoryPort, BuyerRepositoryPort buyerRepositoryPort,
            SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.orderRepositoryPort = orderRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Order consultOrder(User actor, Order order) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR,
                SystemRole.LOGISTICS_OPERATOR);
        provided(order, "Order");
        Order persisted = found(orderRepositoryPort.findById(idOf(order.getId(), "Order")), "Order", order.getId());
        if (SystemRole.BUYER.equals(actor.getRole()) && !persisted.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only consult their own orders.");
        }
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            if (!persisted.containsProductOf(seller)) {
                throw new UnauthorizedOperationException("A seller can only consult orders containing their products.");
            }
        }
        return persisted;
    }

    @Override
    public List<Order> consultAllOrders(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR);
        return orderRepositoryPort.findAll();
    }

    @Override
    public List<Order> consultMyOrders(User actor) {
        authorize(actor, SystemRole.BUYER, SystemRole.SELLER);
        if (SystemRole.BUYER.equals(actor.getRole())) {
            Buyer buyer = found(buyerRepositoryPort.findByUserId(actor.getId()), "Buyer", actor.getId());
            return orderRepositoryPort.findByBuyer(buyer);
        }
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        List<Order> mine = new ArrayList<>();
        for (Order candidate : orderRepositoryPort.findAll()) {
            if (candidate.containsProductOf(seller)) {
                mine.add(candidate);
            }
        }
        return mine;
    }
}
