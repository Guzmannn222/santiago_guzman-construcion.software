package application.domain.ports.in;

import application.domain.models.User;
import application.domain.models.Warehouse;
import java.util.List;

/** Input Port: CU-04 consult warehouses. */
public interface ConsultWarehouseUseCase {

    Warehouse consultWarehouse(User actor, Warehouse warehouse);
    List<Warehouse> consultAllWarehouses(User actor);
}
