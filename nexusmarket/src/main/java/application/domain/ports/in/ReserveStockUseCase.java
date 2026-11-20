package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 reserve stock for a pending order (role BUYER, from checkout). */
public interface ReserveStockUseCase {

    void reserveStock(User actor, Inventory inventory, int quantity, String reason);
}
