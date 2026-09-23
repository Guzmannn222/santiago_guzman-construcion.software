package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.enums.WarehouseType;
import com.nexusmarket.model.User;
import com.nexusmarket.model.Warehouse;
import com.nexusmarket.repository.WarehouseRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/** Business rules for {@link Warehouse} management (SDD, CU-04). */
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    /** Use case: UC-12 Register warehouse. Allowed role: ADMINISTRATOR. */
    public Warehouse registerWarehouse(User actor, String location, WarehouseType type) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        Warehouse warehouse = new Warehouse(IdGenerator.next("WHS"), location, type);
        return warehouseRepository.save(warehouse);
    }

    /** Use case: UC-13 Deactivate warehouse. Allowed role: ADMINISTRATOR. */
    public void deactivateWarehouse(User actor, Warehouse warehouse) {
        AccessControl.requireRole(actor, Role.ADMINISTRATOR);
        warehouse.deactivate();
        warehouseRepository.save(warehouse);
    }
}
