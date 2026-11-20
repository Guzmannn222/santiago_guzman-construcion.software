package application.domain.ports.in;

import application.domain.models.Inventory;
import application.domain.models.User;

/** Input Port: CU-07 set the absolute stock after a physical count (roles SELLER, LOGISTICS_OPERATOR). */
public interface AdjustStockUseCase {

    void adjustStock(User actor, Inventory inventory, int newQuantity, String reason);
}
