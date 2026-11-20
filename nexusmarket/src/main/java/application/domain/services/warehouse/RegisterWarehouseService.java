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
import application.domain.ports.in.RegisterWarehouseUseCase;

/** Administrator or logistics operator registers a warehouse (SELLER type needs an active owner). */
@Service
public class RegisterWarehouseService extends AuthorizedService implements RegisterWarehouseUseCase {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public RegisterWarehouseService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            WarehouseRepositoryPort warehouseRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Warehouse registerWarehouse(User actor, String location, WarehouseType type, Seller owner) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.LOGISTICS_OPERATOR);
        provided(type, "Warehouse type");
        Seller persistedOwner = null;
        if (owner != null) {
            persistedOwner = found(sellerRepositoryPort.findById(idOf(owner.getId(), "Seller")), "Seller", owner.getId());
            persistedOwner.ensureActive();
        }
        Warehouse warehouse = warehouseRepositoryPort.save(new Warehouse(location, type, persistedOwner));
        register(actor, OperationType.WAREHOUSE_REGISTRATION, EntityType.WAREHOUSE, warehouse.getId(),
                "location", warehouse.getLocation(), "type", type.getCode());
        return warehouse;
    }
}
