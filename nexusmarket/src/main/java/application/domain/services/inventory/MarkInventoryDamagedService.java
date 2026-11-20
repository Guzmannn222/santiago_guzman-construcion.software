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
import application.domain.ports.in.MarkInventoryDamagedUseCase;

@Service
public class MarkInventoryDamagedService extends AuthorizedService implements MarkInventoryDamagedUseCase {

    private final LoadManagedInventoryService loadManagedInventoryService;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public MarkInventoryDamagedService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            LoadManagedInventoryService loadManagedInventoryService, InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.loadManagedInventoryService = loadManagedInventoryService;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public void markDamaged(User actor, Inventory inventory) {
        authorize(actor, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        Inventory persisted = loadManagedInventoryService.execute(actor, inventory);
        persisted.markDamaged();
        inventoryRepositoryPort.update(persisted);
        register(actor, OperationType.INVENTORY_DAMAGED, EntityType.INVENTORY, persisted.getId());
    }
}
