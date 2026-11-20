package application.domain.services.order;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Order;
import application.domain.models.User;
import application.domain.ports.out.OrderRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal helper: reloads an order; a BUYER may only act on their own orders (administrators on any). */
@Service
public class LoadOrderForBuyerService {

    private final OrderRepositoryPort orderRepositoryPort;

    public LoadOrderForBuyerService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    public Order execute(User actor, Order reference) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Order");
        }
        Order order = orderRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Order", reference.getId()));
        if (!SystemRole.ADMINISTRATOR.equals(actor.getRole()) && !order.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only act on their own orders.");
        }
        return order;
    }
}
