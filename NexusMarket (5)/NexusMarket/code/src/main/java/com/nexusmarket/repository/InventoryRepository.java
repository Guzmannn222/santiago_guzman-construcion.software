package com.nexusmarket.repository;

import java.util.List;
import java.util.Optional;

import com.nexusmarket.model.Inventory;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.Warehouse;

/**
 * Persistence for {@link Inventory} records. An inventory record is keyed by the combination of
 * product and warehouse, since the same product may have stock in several warehouses.
 */
public class InventoryRepository extends InMemoryRepository<Inventory, String> {

    public InventoryRepository() {
        super(inventory -> key(inventory.getProduct(), inventory.getWarehouse()));
    }

    public Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse) {
        return findById(key(product, warehouse));
    }

    public List<Inventory> findByProduct(Product product) {
        return findAll().stream().filter(inv -> inv.getProduct().equals(product)).toList();
    }

    private static String key(Product product, Warehouse warehouse) {
        return product.getId() + "::" + warehouse.getId();
    }
}
