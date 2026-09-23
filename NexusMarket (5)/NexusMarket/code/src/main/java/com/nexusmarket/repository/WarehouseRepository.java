package com.nexusmarket.repository;

import com.nexusmarket.model.Warehouse;

/** Persistence for {@link Warehouse} entities. */
public class WarehouseRepository extends InMemoryRepository<Warehouse, String> {

    public WarehouseRepository() {
        super(Warehouse::getId);
    }
}
