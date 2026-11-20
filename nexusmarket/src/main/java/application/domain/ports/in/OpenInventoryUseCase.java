package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.User;
import application.domain.models.Warehouse;

/** Input Port: CU-07 open an inventory record for a physical product in a warehouse (roles SELLER, LOGISTICS_OPERATOR). */
public interface OpenInventoryUseCase {

    Inventory openInventory(User actor, Product product, Warehouse warehouse, int initialStock);
}
