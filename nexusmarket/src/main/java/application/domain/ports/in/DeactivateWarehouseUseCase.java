package application.domain.ports.in;

import application.domain.models.User;
import application.domain.models.Warehouse;

/** Input Port: CU-04 deactivate a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR). */
public interface DeactivateWarehouseUseCase {

    void deactivateWarehouse(User actor, Warehouse warehouse);
}
