package com.nexusmarket.service;

import com.nexusmarket.enums.Role;
import com.nexusmarket.model.Cart;
import com.nexusmarket.model.CartItem;
import com.nexusmarket.model.Inventory;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.OrderItem;
import com.nexusmarket.model.User;
import com.nexusmarket.model.Warehouse;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.OrderRepository;
import com.nexusmarket.util.AccessControl;
import com.nexusmarket.util.IdGenerator;
import com.nexusmarket.util.InsufficientStockException;

/**
 * Business rules for order creation and lifecycle management (SDD, Domain 7, CU-09).
 * Validates inventory availability before confirming a cart into a formal order.
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository, InventoryRepository inventoryRepository,
            InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.inventoryRepository = inventoryRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Use case: UC-32 Create order from a confirmed cart (reserves stock for every physical
     * product involved). Allowed role: BUYER.
     */
    public Order createOrder(User actor, Cart cart, Warehouse warehouse) {
        AccessControl.requireRole(actor, Role.BUYER);
        cart.confirm();

        for (CartItem item : cart.getItems()) {
            inventoryRepository.findByProductAndWarehouse(item.getProduct(), warehouse)
                    .ifPresent(inventory -> reserveOrFail(actor, inventory, item));
        }

        Order order = new Order(IdGenerator.next("ORD"), cart.getBuyer());
        for (CartItem item : cart.getItems()) {
            order.addItem(new OrderItem(item.getProduct(), item.getQuantity(), item.getUnitPrice()));
        }
        return orderRepository.save(order);
    }

    private void reserveOrFail(User actor, Inventory inventory, CartItem item) {
        if (!inventoryService.checkAvailability(actor, inventory, item.getQuantity())) {
            throw new InsufficientStockException(
                    "Not enough stock available to place an order for " + item.getProduct().getName());
        }
        inventoryService.reserveStock(actor, inventory, item.getQuantity(),
                "Reservation for order of product " + item.getProduct().getId());
    }

    /** Use case: UC-33 Confirm order payment. Allowed role: BUYER. */
    public void confirmPayment(User actor, Order order) {
        AccessControl.requireRole(actor, Role.BUYER);
        order.confirmPayment();
        orderRepository.save(order);
    }

    /** Use case: UC-34 Cancel order (only before it has shipped). Allowed role: BUYER. */
    public void cancelOrder(User actor, Order order) {
        AccessControl.requireRole(actor, Role.BUYER);
        order.cancel();
        orderRepository.save(order);
    }
}
