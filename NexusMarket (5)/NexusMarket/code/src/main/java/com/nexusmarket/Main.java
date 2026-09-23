package com.nexusmarket;

import com.nexusmarket.controller.NexusMarketFacade;
import com.nexusmarket.enums.ProductType;
import com.nexusmarket.enums.Role;
import com.nexusmarket.enums.WarehouseType;
import com.nexusmarket.model.Buyer;
import com.nexusmarket.model.Cart;
import com.nexusmarket.model.Inventory;
import com.nexusmarket.model.Invoice;
import com.nexusmarket.model.Order;
import com.nexusmarket.model.Product;
import com.nexusmarket.model.Seller;
import com.nexusmarket.model.Shipment;
import com.nexusmarket.model.User;
import com.nexusmarket.model.Warehouse;

/**
 * Entry point of the academic delivery. Runs a short end-to-end demo through the service layer,
 * showing how each use case is executed by the user role authorized to perform it
 * (see /code/src/main/java/com/nexusmarket/service/SERVICES.md for the full catalog).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("NexusMarket -- Marketplace System (academic delivery)");
        System.out.println("=======================================================");

        NexusMarketFacade nexusMarket = new NexusMarketFacade();

        // UC-01 / UC-09: an Administrator registers itself, then onboards a Seller.
        User bootstrapAdmin = new User("USR-0", "System Bootstrap", "bootstrap@nexusmarket.com", Role.ADMINISTRATOR);
        User admin = nexusMarket.userService.register(bootstrapAdmin, "Ana Admin", "ana.admin@nexusmarket.com", Role.ADMINISTRATOR);
        User sellerUser = nexusMarket.userService.register(admin, "Carlos Seller", "carlos.seller@nexusmarket.com", Role.SELLER);
        Seller seller = nexusMarket.sellerService.registerSeller(admin, sellerUser, "Carlos Textiles S.A.S.");
        System.out.println("Seller registered: " + seller.getBusinessInfo());

        // UC-12: an Administrator sets up a warehouse for the marketplace.
        Warehouse warehouse = nexusMarket.warehouseService.registerWarehouse(admin, "Bogota, Colombia", WarehouseType.MARKETPLACE);

        // UC-14 / UC-15 / UC-16: the seller registers, configures and publishes a product.
        Product tShirt = nexusMarket.productService.createProduct(sellerUser, seller, "Basic T-Shirt",
                "100% cotton basic t-shirt", 50000, ProductType.PHYSICAL);
        nexusMarket.productService.addVariant(sellerUser, tShirt, "Size", "M");
        nexusMarket.productService.publish(sellerUser, tShirt);
        System.out.println("Product published: " + tShirt.getName() + " ($" + tShirt.getPrice() + ")");

        // UC-21 / UC-22: a Logistics Operator loads the initial stock into the warehouse.
        User logisticsUser = nexusMarket.userService.register(admin, "Luis Logistics", "luis.logistics@nexusmarket.com", Role.LOGISTICS_OPERATOR);
        Inventory inventory = nexusMarket.inventoryService.openInventory(logisticsUser, tShirt, warehouse, 0);
        nexusMarket.inventoryService.receiveStock(logisticsUser, inventory, 20, "Initial stock load");
        System.out.println("Available stock: " + inventory.getStock() + " units");

        // UC-07 / UC-28 / UC-29: a Buyer registers and builds a cart.
        User buyerUser = nexusMarket.userService.register(admin, "Beatriz Buyer", "beatriz.buyer@nexusmarket.com", Role.BUYER);
        Buyer buyer = nexusMarket.buyerService.registerBuyer(buyerUser, "Cra 45 #10-20, Medellin");
        Cart cart = nexusMarket.cartService.createCart(buyerUser, buyer);
        nexusMarket.cartService.addProduct(buyerUser, cart, tShirt, 3);
        System.out.println("Cart total: $" + cart.getTotal());

        // UC-32 / UC-33: the cart is confirmed into an order (reserves stock) and payment is confirmed.
        Order order = nexusMarket.orderService.createOrder(buyerUser, cart, warehouse);
        nexusMarket.orderService.confirmPayment(buyerUser, order);
        System.out.println("Order " + order.getId() + " status: " + order.getStatus());
        System.out.println("Remaining stock after reservation: " + inventory.getStock() + " units");

        // UC-35: an Administrator generates the invoice for the paid order.
        Invoice invoice = nexusMarket.invoiceService.generateInvoice(admin, order, 0.19);
        System.out.println("Invoice total (incl. tax): $" + invoice.getTotal());

        // UC-37 / UC-38 / UC-41: the Logistics Operator ships and delivers the order.
        Shipment shipment = nexusMarket.shipmentService.createShipment(logisticsUser, order, buyer.getPrimaryAddress());
        nexusMarket.shipmentService.prepare(logisticsUser, shipment);
        nexusMarket.shipmentService.dispatch(logisticsUser, shipment);
        nexusMarket.shipmentService.confirmDelivery(logisticsUser, shipment);
        System.out.println("Shipment status: " + shipment.getStatus() + " | Order status: " + order.getStatus());

        System.out.println("=======================================================");
        System.out.println("Demo finished. See SDD/ and service/SERVICES.md for full documentation.");
    }
}
