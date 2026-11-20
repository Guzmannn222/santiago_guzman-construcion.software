package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 return units to the warehouse (roles LOGISTICS_OPERATOR, SELLER). */
public interface ReturnStockUseCase {

    void returnStock(User actor, Inventory inventory, int quantity, String reason);
}
