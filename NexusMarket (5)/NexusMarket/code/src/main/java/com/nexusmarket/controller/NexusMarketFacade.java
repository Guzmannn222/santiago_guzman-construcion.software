package com.nexusmarket.controller;

import com.nexusmarket.repository.BuyerRepository;
import com.nexusmarket.repository.CartRepository;
import com.nexusmarket.repository.InventoryRepository;
import com.nexusmarket.repository.InvoiceRepository;
import com.nexusmarket.repository.OrderRepository;
import com.nexusmarket.repository.ProductRepository;
import com.nexusmarket.repository.RefundRepository;
import com.nexusmarket.repository.ReturnRepository;
import com.nexusmarket.repository.SellerRepository;
import com.nexusmarket.repository.ShipmentRepository;
import com.nexusmarket.repository.UserRepository;
import com.nexusmarket.repository.WarehouseRepository;
import com.nexusmarket.service.BuyerService;
import com.nexusmarket.service.CartService;
import com.nexusmarket.service.InventoryService;
import com.nexusmarket.service.InvoiceService;
import com.nexusmarket.service.OrderService;
import com.nexusmarket.service.ProductService;
import com.nexusmarket.service.RefundService;
import com.nexusmarket.service.ReportService;
import com.nexusmarket.service.ReturnService;
import com.nexusmarket.service.SellerService;
import com.nexusmarket.service.ShipmentService;
import com.nexusmarket.service.UserService;
import com.nexusmarket.service.WarehouseService;

/**
 * Entry point and coordination layer for the academic delivery (SDD, package {@code controller}).
 * Wires repositories and services together and exposes them ready to use, translating the console
 * menu (or a future API) into calls onto the {@code service} layer, as required by the layered
 * architecture proposed in SDD 28-29.
 */
public class NexusMarketFacade {

    private final UserRepository userRepository = new UserRepository();
    private final BuyerRepository buyerRepository = new BuyerRepository();
    private final SellerRepository sellerRepository = new SellerRepository();
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final ProductRepository productRepository = new ProductRepository();
    private final InventoryRepository inventoryRepository = new InventoryRepository();
    private final CartRepository cartRepository = new CartRepository();
    private final OrderRepository orderRepository = new OrderRepository();
    private final InvoiceRepository invoiceRepository = new InvoiceRepository();
    private final ShipmentRepository shipmentRepository = new ShipmentRepository();
    private final ReturnRepository returnRepository = new ReturnRepository();
    private final RefundRepository refundRepository = new RefundRepository();

    public final UserService userService = new UserService(userRepository);
    public final BuyerService buyerService = new BuyerService(buyerRepository);
    public final SellerService sellerService = new SellerService(sellerRepository);
    public final WarehouseService warehouseService = new WarehouseService(warehouseRepository);
    public final ProductService productService = new ProductService(productRepository);
    public final InventoryService inventoryService = new InventoryService(inventoryRepository);
    public final CartService cartService = new CartService(cartRepository);
    public final OrderService orderService = new OrderService(orderRepository, inventoryRepository, inventoryService);
    public final InvoiceService invoiceService = new InvoiceService(invoiceRepository);
    public final ShipmentService shipmentService = new ShipmentService(shipmentRepository);
    public final ReturnService returnService = new ReturnService(returnRepository);
    public final RefundService refundService = new RefundService(refundRepository);
    public final ReportService reportService = new ReportService();

    public BuyerRepository buyers() { return buyerRepository; }
    public WarehouseRepository warehouses() { return warehouseRepository; }
}
