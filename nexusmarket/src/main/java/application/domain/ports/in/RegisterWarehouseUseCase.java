package application.domain.ports.in;

import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Warehouse;
import application.domain.valueobjects.WarehouseType;

/** Input Port: CU-04 register a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR). */
public interface RegisterWarehouseUseCase {

    Warehouse registerWarehouse(User actor, String location, WarehouseType type, Seller owner);
}
