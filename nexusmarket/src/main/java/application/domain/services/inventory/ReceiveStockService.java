package application.domain.services.inventory;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.ReceiveStockUseCase;

/** Registers incoming stock. */
@Service
public class ReceiveStockService extends AuthorizedService implements ReceiveStockUseCase {

    private final LoadManagedInventoryService loadManagedInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public ReceiveStockService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedInventoryService loadManagedInventoryService, InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.loadManagedInventoryService = loadManagedInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public void receiveStock(User actor, Inventory inventory, int quantity, String reason) {
        authorize(actor, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        Inventory persisted = loadManagedInventoryService.execute(actor, inventory);
        persisted.getWarehouse().ensureActive();
        persisted.inbound(quantity);
        persisted.recordMovement(new InventoryMovement(InventoryMovementType.INBOUND, quantity, reason));
        inventoryRepositoryPort.update(persisted);
        register(actor, OperationType.STOCK_RECEIVED, EntityType.INVENTORY, persisted.getId(), "quantity", quantity,
                "reason", reason, "stock", persisted.getStock());
    }
}
