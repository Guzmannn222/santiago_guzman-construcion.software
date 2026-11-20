package application.domain.ports.out;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Warehouse;
import java.util.List;
import java.util.Optional;

/** Output port: persistence of Inventory aggregates. */
public interface InventoryRepositoryPort {

    Inventory save(Inventory inventory);
    void update(Inventory inventory);
    Optional<Inventory> findById(Long id);
    List<Inventory> findAll();
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);
    List<Inventory> findByProductId(Long productId);
    List<Inventory> findByWarehouseId(Long warehouseId);
}
