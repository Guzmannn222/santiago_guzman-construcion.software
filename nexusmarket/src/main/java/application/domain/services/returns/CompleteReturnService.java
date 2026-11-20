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
import application.domain.ports.in.CompleteReturnUseCase;

/** Logistics receives the goods; physical units go back into the order's warehouse. */
@Service
public class CompleteReturnService extends AuthorizedService implements CompleteReturnUseCase {

    private final LoadReturnForReviewService loadReturnForReviewService;
    private final ReturnRepositoryPort returnRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public CompleteReturnService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadReturnForReviewService loadReturnForReviewService, ReturnRepositoryPort returnRepositoryPort,
            InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.loadReturnForReviewService = loadReturnForReviewService;
        this.returnRepositoryPort = returnRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public void complete(User actor, Return productReturn) {
        authorize(actor, SystemRole.LOGISTICS_OPERATOR, SystemRole.ADMINISTRATOR);
        Return persisted = loadReturnForReviewService.execute(actor, productReturn);
        persisted.complete();
        returnRepositoryPort.update(persisted);
        Order order = persisted.getOrder();
        if (order.getWarehouse() != null) {
            for (ReturnItem item : persisted.returnedLines()) {
                if (!item.getProduct().isPhysical()) {
                    continue;
                }
                inventoryRepositoryPort.findByProductAndWarehouse(item.getProduct(), order.getWarehouse())
                        .ifPresent(inventory -> {
                            inventory.returnStock(item.getQuantity());
                            inventory.recordMovement(new InventoryMovement(InventoryMovementType.RETURN,
                                    item.getQuantity(), "Return " + persisted.getId()));
                            inventoryRepositoryPort.update(inventory);
                        });
            }
        }
        register(actor, OperationType.RETURN_COMPLETION, EntityType.RETURN, persisted.getId(),
                "orderId", persisted.getOrder().getId(), "status", persisted.getStatus().getCode(),
                "restockedItems", persisted.returnedLines().size());
    }
}
