package application.domain.services.warehouse;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.ports.out.*;
import application.domain.services.authorization.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.util.*;
import org.springframework.stereotype.Service;
import application.domain.services.support.AuthorizedService;
import application.domain.ports.in.DeactivateWarehouseUseCase;

/** A warehouse that still holds stock cannot be deactivated. */
@Service
public class DeactivateWarehouseService extends AuthorizedService implements DeactivateWarehouseUseCase {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;

    public DeactivateWarehouseService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            WarehouseRepositoryPort warehouseRepositoryPort, InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public void deactivateWarehouse(User actor, Warehouse warehouse) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.LOGISTICS_OPERATOR);
        provided(warehouse, "Warehouse");
        Warehouse persisted = found(warehouseRepositoryPort.findById(idOf(warehouse.getId(), "Warehouse")),
                "Warehouse", warehouse.getId());
        for (Inventory inventory : inventoryRepositoryPort.findByWarehouseId(persisted.getId())) {
            if (inventory.getStock() > 0) {
                throw new InvalidWarehouseStatusException("The warehouse still holds stock and cannot be deactivated.");
            }
        }
        persisted.deactivate();
        warehouseRepositoryPort.update(persisted);
        register(actor, OperationType.WAREHOUSE_DEACTIVATION, EntityType.WAREHOUSE, persisted.getId());
    }
}
