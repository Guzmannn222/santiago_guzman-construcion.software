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
import application.domain.ports.in.ConsultInventoryUseCase;

/** Sellers see inventories of their own products; operational roles see all. */
@Service
public class ConsultInventoryService extends AuthorizedService implements ConsultInventoryUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;

    public ConsultInventoryService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            InventoryRepositoryPort inventoryRepositoryPort, SellerRepositoryPort sellerRepositoryPort,
            ProductRepositoryPort productRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort) {
        super(s, r, t);
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
    }

    private void authorizeConsult(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR,
                SystemRole.SELLER);
    }

    private void ensureVisible(User actor, Inventory inventory) {
        if (SystemRole.SELLER.equals(actor.getRole())
                && !(inventory.getProduct() != null && inventory.getProduct().getSeller() != null
                        && inventory.getProduct().getSeller().isOwnedBy(actor))) {
            throw new UnauthorizedOperationException("A seller can only consult the inventory of their own products.");
        }
    }

    @Override
    public Inventory consultInventory(User actor, Inventory inventory) {
        authorizeConsult(actor);
        provided(inventory, "Inventory");
        Inventory persisted = found(inventoryRepositoryPort.findById(idOf(inventory.getId(), "Inventory")),
                "Inventory", inventory.getId());
        ensureVisible(actor, persisted);
        return persisted;
    }

    @Override
    public Inventory consultByProductAndWarehouse(User actor, Product product, Warehouse warehouse) {
        authorizeConsult(actor);
        provided(product, "Product");
        provided(warehouse, "Warehouse");
        Product p = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product", product.getId());
        Warehouse w = found(warehouseRepositoryPort.findById(idOf(warehouse.getId(), "Warehouse")), "Warehouse",
                warehouse.getId());
        Inventory persisted = inventoryRepositoryPort.findByProductAndWarehouse(p, w)
                .orElseThrow(() -> new EntityNotFoundException("Inventory"));
        ensureVisible(actor, persisted);
        return persisted;
    }

    @Override
    public List<Inventory> consultAllInventory(User actor) {
        authorize(actor, SystemRole.ADMINISTRATOR, SystemRole.SUPERVISOR, SystemRole.LOGISTICS_OPERATOR);
        return inventoryRepositoryPort.findAll();
    }

    @Override
    public List<Inventory> consultMyInventory(User actor) {
        authorize(actor, SystemRole.SELLER);
        Seller seller = found(sellerRepositoryPort.findByUserId(actor.getId()), "Seller", actor.getId());
        List<Inventory> mine = new ArrayList<>();
        for (Product product : productRepositoryPort.findBySellerId(seller.getId())) {
            mine.addAll(inventoryRepositoryPort.findByProductId(product.getId()));
        }
        return mine;
    }
}
