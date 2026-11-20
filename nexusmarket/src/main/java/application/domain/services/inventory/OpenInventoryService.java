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
import application.domain.ports.in.OpenInventoryUseCase;

/** Opens the stock record of a physical product in an active warehouse (one per product+warehouse). */
@Service
public class OpenInventoryService extends AuthorizedService implements OpenInventoryUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final BusinessConfigurationPort businessConfigurationPort;

    public OpenInventoryService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            ProductRepositoryPort productRepositoryPort, WarehouseRepositoryPort warehouseRepositoryPort,
            InventoryRepositoryPort inventoryRepositoryPort, BusinessConfigurationPort businessConfigurationPort) {
        super(s, r, t);
        this.productRepositoryPort = productRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.businessConfigurationPort = businessConfigurationPort;
    }

    @Override
    public Inventory openInventory(User actor, Product product, Warehouse warehouse, int initialStock) {
        authorize(actor, SystemRole.SELLER, SystemRole.LOGISTICS_OPERATOR);
        provided(product, "Product");
        provided(warehouse, "Warehouse");
        Product persistedProduct = found(productRepositoryPort.findById(idOf(product.getId(), "Product")), "Product",
                product.getId());
        Warehouse persistedWarehouse = found(warehouseRepositoryPort.findById(idOf(warehouse.getId(), "Warehouse")),
                "Warehouse", warehouse.getId());
        if (SystemRole.SELLER.equals(actor.getRole()) && !persistedProduct.getSeller().isOwnedBy(actor)) {
            throw new UnauthorizedOperationException("A seller can only open inventory for their own products.");
        }
        persistedWarehouse.ensureActive();
        if (persistedWarehouse.getOwner() != null
                && !persistedWarehouse.getOwner().getId().equals(persistedProduct.getSeller().getId())) {
            throw new InvalidInventoryException("The warehouse belongs to a different seller than the product.");
        }
        if (inventoryRepositoryPort.findByProductAndWarehouse(persistedProduct, persistedWarehouse).isPresent()) {
            throw new DuplicateResourceException("An inventory already exists for this product in this warehouse.");
        }
        Inventory inventory = new Inventory(persistedProduct, persistedWarehouse, initialStock);
        inventory.setLowStockThreshold(businessConfigurationPort.getLowStockThreshold());
        if (initialStock > 0) {
            inventory.recordMovement(new InventoryMovement(InventoryMovementType.INBOUND, initialStock, "Initial stock"));
        }
        inventory.refreshStatus();
        Inventory saved = inventoryRepositoryPort.save(inventory);
        register(actor, OperationType.INVENTORY_OPENING, EntityType.INVENTORY, saved.getId(),
                "productId", persistedProduct.getId(), "warehouseId", persistedWarehouse.getId(),
                "initialStock", initialStock);
        return saved;
    }
}
