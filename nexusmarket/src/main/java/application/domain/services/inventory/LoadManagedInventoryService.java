package application.domain.services.inventory;

import application.domain.exceptions.EntityNotFoundException;
import application.domain.exceptions.UnauthorizedOperationException;
import application.domain.models.Inventory;
import application.domain.models.User;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.valueobjects.SystemRole;
import org.springframework.stereotype.Service;

/** Internal helper: reloads an inventory; a SELLER may only touch inventories of their own products. */
@Service
public class LoadManagedInventoryService {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public LoadManagedInventoryService(InventoryRepositoryPort inventoryRepositoryPort) {
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    public Inventory execute(User actor, Inventory reference) {
        if (reference == null || reference.getId() == null) {
            throw new EntityNotFoundException("Inventory");
        }
        Inventory inventory = inventoryRepositoryPort.findById(reference.getId())
                .orElseThrow(() -> new EntityNotFoundException("Inventory", reference.getId()));
        if (SystemRole.SELLER.equals(actor.getRole())) {
            boolean owner = inventory.getProduct() != null && inventory.getProduct().getSeller() != null
                    && inventory.getProduct().getSeller().isOwnedBy(actor);
            if (!owner) {
                throw new UnauthorizedOperationException("A seller can only manage the inventory of their own products.");
            }
        }
        return inventory;
    }
}
