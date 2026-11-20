package application.domain.services.cart;

import application.domain.exceptions.InsufficientStockException;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.InventoryStatus;
import org.springframework.stereotype.Service;

/**
 * Cart-time stock rule: a buyer cannot put more units of a PHYSICAL product in the cart than are
 * available (sum of the sellable stock of every active warehouse). Digital products have no stock.
 */
@Service
public class CartStockGuard {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public CartStockGuard(InventoryRepositoryPort inventoryRepositoryPort) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    /** @param totalQuantity the quantity the cart line would have after the operation */
    public void ensureAvailable(Product product, int totalQuantity) {
        if (product == null || !product.isPhysical()) {
            return;
        }
        int available = availableStock(product);
        if (totalQuantity > available) {
            throw new InsufficientStockException("Not enough stock for product " + product.getId() + " ("
                    + product.getName() + "): requested " + totalQuantity + " but only " + available
                    + " unit(s) are available.");
        }
    }

    public int availableStock(Product product) {
        int available = 0;
        for (Inventory inventory : inventoryRepositoryPort.findByProductId(product.getId())) {
            boolean sellable = inventory.getWarehouse() != null && inventory.getWarehouse().isActive()
                    && !InventoryStatus.DAMAGED.equals(inventory.getStatus());
            if (sellable) {
                available += inventory.getStock();
            }
        }
        return available;
    }
}
