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
import application.domain.ports.in.AdjustStockUseCase;

/** Sets the physical stock count (inventory correction); a reason is mandatory. */
@Service
public class AdjustStockService extends AuthorizedService implements AdjustStockUseCase {

    private final LoadManagedInventoryService loadManagedInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public AdjustStockService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedInventoryService loadManagedInventoryService, InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.loadManagedInventoryService = loadManagedInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public void adjustStock(User actor, Inventory inventory, int newQuantity, String reason) {
        authorize(actor, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        if (reason == null || reason.isBlank()) {
            throw new InvalidInventoryException("A reason is required to adjust stock.");
        }
        Inventory persisted = loadManagedInventoryService.execute(actor, inventory);
        int previous = persisted.getStock();
        persisted.adjust(newQuantity);
        persisted.recordMovement(new InventoryMovement(InventoryMovementType.ADJUSTMENT, newQuantity, reason));
        inventoryRepositoryPort.update(persisted);
        register(actor, OperationType.STOCK_ADJUSTED, EntityType.INVENTORY, persisted.getId(),
                "previousStock", previous, "newStock", newQuantity, "reason", reason);
    }
}
