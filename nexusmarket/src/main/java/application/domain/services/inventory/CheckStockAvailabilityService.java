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
import application.domain.ports.in.CheckStockAvailabilityUseCase;

@Service
public class CheckStockAvailabilityService extends AuthorizedService implements CheckStockAvailabilityUseCase {

    private final InventoryRepositoryPort inventoryRepositoryPort;

    public CheckStockAvailabilityService(ValidateUserAuthorizationStatusService s, ValidateRoleAuthorizationService r,
            RegisterOperationAndAuditService t,
            InventoryRepositoryPort inventoryRepositoryPort) {
        super(s, r, t);
        this.inventoryRepositoryPort = inventoryRepositoryPort;
    }

    @Override
    public boolean checkAvailability(User actor, Inventory inventory, int quantity) {
        validateStatus.execute(actor);
        if (quantity <= 0) {
            throw new InvalidQuantityException("The quantity to check must be greater than zero.");
        }
        provided(inventory, "Inventory");
        Inventory persisted = found(inventoryRepositoryPort.findById(idOf(inventory.getId(), "Inventory")),
                "Inventory", inventory.getId());
        return persisted.canFulfill(quantity);
    }
}
