package com.nexusmarket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nexusmarket.enums.ProductType;
import com.nexusmarket.enums.WarehouseType;
import com.nexusmarket.model.Inventory;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.Warehouse;
import com.nexusmarket.util.InsufficientStockException;

/**
 * Unit test for the critical business rule: "negative stock is never allowed"
 * (SDD section 16.2). Base for the tests suggested in section 31.1.
 */
class InventoryTest {

    @Test
    void shouldNotAllowWithdrawingMoreStockThanAvailable() {
        Product product = new Product("P1", null, "T-Shirt", "Basic t-shirt", 50000, ProductType.PHYSICAL);
        Warehouse warehouse = new Warehouse("W1", "Bogota", WarehouseType.MARKETPLACE);
        Inventory inventory = new Inventory(product, warehouse, 5);

        assertThrows(InsufficientStockException.class, () -> inventory.withdraw(10));
    }

    @Test
    void shouldReserveStockWhenEnoughIsAvailable() {
        Product product = new Product("P2", null, "Mug", "Ceramic mug", 20000, ProductType.PHYSICAL);
        Warehouse warehouse = new Warehouse("W2", "Medellin", WarehouseType.MARKETPLACE);
        Inventory inventory = new Inventory(product, warehouse, 10);

        inventory.reserve(4);

        assertEquals(6, inventory.getStock());
    }
}
