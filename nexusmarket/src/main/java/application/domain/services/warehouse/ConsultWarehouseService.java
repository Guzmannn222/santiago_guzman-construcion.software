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
import application.domain.ports.in.ConsultWarehouseUseCase;

/** Sellers see only the warehouses they own; operational roles see all. */
@Service
public class ConsultWarehouseService extends AuthorizedService implements ConsultWarehouseUseCase {

    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;

    public ConsultWarehouseService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            WarehouseRepositoryPort warehouseRepositoryPort, SellerRepositoryPort sellerRepositoryPort) {
        super(s, r, t);
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
    }

    @Override
    public Warehouse consultWarehouse(User actor, Warehouse warehouse) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.SELLER);
        provided(warehouse, "Warehouse");
        Warehouse persisted = found(warehouseRepositoryPort.findById(idOf(warehouse.getId(), "Warehouse")),
                "Warehouse", warehouse.getId());
        if (SystemRole.SELLER.equals(actor.getRole())
                && (persisted.getOwner() == null || !persisted.getOwner().isOwnedBy(actor))) {
            throw new UnauthorizedOperationException("A seller can only consult their own warehouses.");
        }
        return persisted;
    }

    @Override
    public List<Warehouse> consultAllWarehouses(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.SELLER);
        if (SystemRole.SELLER.equals(actor.getRole())) {
            Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
            return warehouseRepositoryPort.findByOwnerId(seller.getId());
        }
        return warehouseRepositoryPort.findAll();
    }
}
