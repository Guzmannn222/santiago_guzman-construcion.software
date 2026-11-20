package application.domain.services.returns;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.RequestReturnUseCase;

/** A buyer requests the return of a delivered order (one active return per order). */
@Service
public class RequestReturnService extends AuthorizedService implements RequestReturnUseCase {

    private final OrderRepositoryPort orderRepositoryPort;
    private final ReturnRepositoryPort returnRepositoryPort;

    public RequestReturnService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            OrderRepositoryPort orderRepositoryPort, ReturnRepositoryPort returnRepositoryPort) {
        super(s, r, t);
        this.orderRepositoryPort = orderRepositoryPort;
        this.returnRepositoryPort = returnRepositoryPort;
    }

    @Override
    public Return requestReturn(User actor, Order order, String reason, List<ReturnItem> items) {
        authorize(actor, SystemRole.BUYER);
        provided(order, "Order");
        Order persisted = found(orderRepositoryPort.findById(idOf(order.getId(), "Order")), "Order", order.getId());
        if (!persisted.isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A buyer can only request returns of their own orders.");
        }
        for (Return existing : returnRepositoryPort.findByOrderId(persisted.getId())) {
            if (!ReturnStatus.REJECTED.equals(existing.getStatus())) {
                throw new DuplicateResourceException("The order already has an active return request.");
            }
        }
        Return saved = returnRepositoryPort.save(new Return(persisted, reason, items));
        List<String> returned = new ArrayList<>();
        for (ReturnItem line : saved.returnedLines()) {
            returned.add(line.getProduct().getId() + "x" + line.getQuantity());
        }
        register(actor, OperationType.RETURN_REQUEST, EntityType.RETURN, saved.getId(),
                "orderId", persisted.getId(), "reason", saved.getReason(), "items", returned);
        return saved;
    }
}
