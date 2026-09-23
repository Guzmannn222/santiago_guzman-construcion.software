package com.nexusmarket.service;

import com.nexusmarket.enums.InventoryMovementType;
import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Inventory;
import com.nexusmarket.model.InventoryMovement;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.User;
import com.nexusmarket.model.Warehouse;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;

/**
 * Business rules for inventory management (SDD, Domain 6, CU-07).
 * All stock validations are centralized here to avoid duplication in other layers (SDD 31).
 */
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    /** Use case: UC-21 Open an inventory record for a product in a warehouse. Allowed role: LOGISTICS_OPERATOR. */
    public Inventory openInventory(User actor, Product product, Warehouse warehouse, int initialStock) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        return inventoryRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> inventoryRepository.save(new Inventory(product, warehouse, initialStock)));
    }

    /** Use case: UC-22 Receive/inbound stock. Allowed role: LOGISTICS_OPERATOR. */
    public synchronized void receiveStock(User actor, Inventory inventory, int quantity, String reason) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        inventory.inbound(quantity);
        register(inventory, InventoryMovementType.INBOUND, quantity, reason);
    }

    /** Use case: UC-23 Reserve stock for a pending order. Allowed role: BUYER (triggered from checkout). */
    public synchronized void reserveStock(User actor, Inventory inventory, int quantity, String reason) {
        AccessControl.requireRole(actor, Role.BUYER);
        inventory.reserve(quantity);
        register(inventory, InventoryMovementType.RESERVATION, quantity, reason);
    }

    /** Use case: UC-24 Confirm sale outbound movement. Allowed role: LOGISTICS_OPERATOR. */
    public synchronized void confirmSaleOutbound(User actor, Inventory inventory, int quantity, String reason) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        register(inventory, InventoryMovementType.SALE_OUTBOUND, quantity, reason);
    }

    /** Use case: UC-25 Adjust stock after a physical count. Allowed role: LOGISTICS_OPERATOR. */
    public synchronized void adjustStock(User actor, Inventory inventory, int newQuantity, String reason) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        inventory.adjust(newQuantity);
        register(inventory, InventoryMovementType.ADJUSTMENT, newQuantity, reason);
    }

    /** Use case: UC-26 Return stock to the warehouse after an approved return. Allowed role: LOGISTICS_OPERATOR. */
    public synchronized void returnStock(User actor, Inventory inventory, int quantity, String reason) {
        AccessControl.requireRole(actor, Role.LOGISTICS_OPERATOR);
        inventory.returnStock(quantity);
        register(inventory, InventoryMovementType.RETURN, quantity, reason);
    }

    /** Use case: UC-27 Check stock availability before adding to cart. Allowed role: BUYER. */
    public boolean checkAvailability(User actor, Inventory inventory, int quantity) {
        AccessControl.requireRole(actor, Role.BUYER);
        return inventory.getStock() >= quantity;
    }

    /** Internal helper reused by every movement service above; not exposed as its own use case. */
    private void register(Inventory inventory, InventoryMovementType type, int quantity, String reason) {
        InventoryMovement movement = new InventoryMovement(IdGenerator.next("MOV"), type, quantity, reason);
        inventory.recordMovement(movement);
        inventoryRepository.save(inventory);
    }
}
