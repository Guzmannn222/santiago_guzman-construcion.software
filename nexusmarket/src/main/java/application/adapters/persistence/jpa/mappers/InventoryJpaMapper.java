package application.adapters.persistence.jpa.mappers;

import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.domain.models.Inventory;
import application.domain.models.InventoryMovement;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import application.domain.valueobjects.InventoryMovementType;
import application.domain.valueobjects.InventoryStatus;
import java.util.ArrayList;
import java.util.List;

/** Bidirectional mapper between the Inventory Domain Model and its JPA entities. */
public final class InventoryJpaMapper {

    private InventoryJpaMapper() {
    }

    public static InventoryJpaEntity toEntity(Inventory domain) {
        if (domain == null) {
            return null;
        }
        InventoryJpaEntity entity = new InventoryJpaEntity();
        entity.setId(domain.getId());
        entity.setProductId(domain.getProduct() != null ? domain.getProduct().getId() : null);
        entity.setWarehouseId(domain.getWarehouse() != null ? domain.getWarehouse().getId() : null);
        entity.setStock(domain.getStock());
        entity.setStatus(domain.getStatus() != null ? domain.getStatus().getCode() : null);
        entity.setLowStockThreshold(domain.getLowStockThreshold());
        return entity;
    }

    public static List<InventoryMovementJpaEntity> toMovementEntities(Long inventoryId, List<InventoryMovement> movements) {
        List<InventoryMovementJpaEntity> result = new ArrayList<>();
        if (movements == null) {
            return result;
        }
        for (InventoryMovement movement : movements) {
            InventoryMovementJpaEntity row = new InventoryMovementJpaEntity();
            row.setInventoryId(inventoryId);
            row.setType(movement.getType() != null ? movement.getType().getCode() : null);
            row.setQuantity(movement.getQuantity());
            row.setDate(movement.getDate());
            row.setReason(movement.getReason());
            result.add(row);
        }
        return result;
    }

    public static Inventory toDomain(InventoryJpaEntity entity, Product product, Warehouse warehouse,
                                     List<InventoryMovementJpaEntity> movements) {
        if (entity == null) {
            return null;
        }
        Inventory domain = new Inventory();
        domain.setId(entity.getId());
        domain.setProduct(product);
        domain.setWarehouse(warehouse);
        domain.setStock(entity.getStock() != null ? entity.getStock() : 0);
        domain.setStatus(entity.getStatus() != null ? InventoryStatus.fromCode(entity.getStatus()) : null);
        domain.setLowStockThreshold(entity.getLowStockThreshold() != null
                ? entity.getLowStockThreshold() : Inventory.DEFAULT_LOW_STOCK_THRESHOLD);
        List<InventoryMovement> list = new ArrayList<>();
        if (movements != null) {
            for (InventoryMovementJpaEntity row : movements) {
                InventoryMovement movement = new InventoryMovement();
                movement.setType(row.getType() != null ? InventoryMovementType.fromCode(row.getType()) : null);
                movement.setQuantity(row.getQuantity() != null ? row.getQuantity() : 0);
                movement.setDate(row.getDate());
                movement.setReason(row.getReason());
                list.add(movement);
            }
        }
        domain.setMovements(list);
        return domain;
    }
}
