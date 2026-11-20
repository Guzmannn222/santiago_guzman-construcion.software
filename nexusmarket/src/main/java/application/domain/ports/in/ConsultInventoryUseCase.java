package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/** Input Port: CU-07 consult inventory. SELLER only sees inventory of its own products or warehouses. */
public interface ConsultInventoryUseCase {

    Inventory consultInventory(User actor, Inventory inventory);
    Inventory consultByProductAndWarehouse(User actor, Product product, Warehouse warehouse);
    List<Inventory> consultAllInventory(User actor);
    List<Inventory> consultMyInventory(User actor);
}
