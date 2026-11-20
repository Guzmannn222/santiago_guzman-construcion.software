package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 mark an inventory record as damaged (roles SELLER, LOGISTICS_OPERATOR). */
public interface MarkInventoryDamagedUseCase {

    void markDamaged(User actor, Inventory inventory);
}
