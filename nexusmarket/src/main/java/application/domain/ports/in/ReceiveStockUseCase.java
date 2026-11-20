package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 receive stock (roles SELLER, LOGISTICS_OPERATOR). */
public interface ReceiveStockUseCase {

    void receiveStock(User actor, Inventory inventory, int quantity, String reason);
}
