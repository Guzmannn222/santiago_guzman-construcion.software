package application.domain;

import static org.junit.jupiter.api.Assertions.*;

import application.domain.exceptions.*;
import application.domain.models.*;
import application.domain.services.buyer.*;
import application.domain.services.cart.*;
import application.domain.services.inventory.*;
import application.domain.services.invoice.*;
import application.domain.services.order.*;
import application.domain.services.product.*;
import application.domain.services.refund.*;
import application.domain.services.report.*;
import application.domain.services.returns.*;
import application.domain.services.seller.*;
import application.domain.services.shipment.*;
import application.domain.services.user.*;
import application.domain.services.warehouse.*;
import application.domain.services.operation.*;
import application.domain.valueobjects.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
class CartOrderServicesTest {

    private Fixture fx;
    private Seller seller;
    private Product physical;
    private Product digital;
    private Inventory inventory;
    private Buyer buyer;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        seller = fx.newSeller();
        physical = fx.newProduct(seller, ProductType.PHYSICAL, "10000", true);
        digital = fx.newProduct(seller, ProductType.DIGITAL, "5000", true);
        inventory = fx.newInventory(physical, fx.warehouseOf(seller), 10);
        buyer = fx.newBuyer();
    }

    private LoadOwnedCartService loadCart() { return new LoadOwnedCartService(fx.cartPort); }

    private AddCartItemService add() {
        return new AddCartItemService(fx.validateStatus, fx.validateRole, fx.traceability, loadCart(), fx.cartPort, fx.productPort,
                new CartStockGuard(fx.inventoryPort));
    }

    private CreateOrderService createOrder() {
        return new CreateOrderService(fx.validateStatus, fx.validateRole, fx.traceability, fx.cartPort, fx.productPort,
                fx.warehousePort, fx.inventoryPort, fx.orderPort);
    }

    private LoadOrderForBuyerService loadOrder() { return new LoadOrderForBuyerService(fx.orderPort); }

    private ConfirmOrderPaymentService pay() {
        return new ConfirmOrderPaymentService(fx.validateStatus, fx.validateRole, fx.traceability, loadOrder(),
                fx.orderPort, fx.notificationPort);
    }

    private CancelOrderService cancel() {
        return new CancelOrderService(fx.validateStatus, fx.validateRole, fx.traceability, loadOrder(), fx.orderPort,
                fx.inventoryPort, fx.invoicePort);
    }

    @Test
    void cartServicesEnforceOwnershipAndQuantityRules() {
        User user = buyer.getUser();
        Buyer other = fx.newBuyer();
        add().addProduct(user, buyer.getCart(), physical, 2);
        add().addProduct(user, buyer.getCart(), physical, 3);
        assertEquals(5, fx.carts.findById(buyer.getCart().getId()).get().getItems().get(0).getQuantity());
        assertThrows(InvalidCartException.class, () -> add().addProduct(user, buyer.getCart(), physical, 0));
        assertThrows(InvalidCartException.class, () -> add().addProduct(user, buyer.getCart(), digital, 101));
        assertThrows(UnauthorizedOperationException.class, () -> add().addProduct(other.getUser(), buyer.getCart(), physical, 1));
        assertThrows(UnauthorizedOperationException.class, () -> add().addProduct(seller.getUser(), buyer.getCart(), physical, 1));
        Product draft = fx.newProduct(seller, ProductType.PHYSICAL, "1", false);
        assertThrows(DomainException.class, () -> add().addProduct(user, buyer.getCart(), draft, 1));
        assertThrows(EntityNotFoundException.class, () -> add().addProduct(user, buyer.getCart(), new Product(), 1));

        UpdateCartItemQuantityService update = new UpdateCartItemQuantityService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadCart(), fx.cartPort, fx.productPort, new CartStockGuard(fx.inventoryPort));
        update.updateQuantity(user, buyer.getCart(), physical, 7);
        assertEquals(7, fx.carts.findById(buyer.getCart().getId()).get().getItems().get(0).getQuantity());

        RemoveCartItemService remove = new RemoveCartItemService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadCart(), fx.cartPort, fx.productPort);
        remove.removeProduct(user, buyer.getCart(), physical);
        assertTrue(fx.carts.findById(buyer.getCart().getId()).get().getItems().isEmpty());
    }

    @Test
    void consultCart() {
        ConsultCartService consult = new ConsultCartService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.cartPort, fx.buyerPort);
        assertSame(buyer.getCart(), consult.consultMyCart(buyer.getUser()));
        assertSame(buyer.getCart(), consult.consultCart(fx.admin(), buyer.getCart()));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultCart(fx.newBuyer().getUser(), buyer.getCart()));
    }

    @Test
    @DisplayName("Creating an order reserves stock from the (auto-chosen) warehouse and empties the cart")
    void createOrderReservesStock() {
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 4);
        add().addProduct(buyer.getUser(), buyer.getCart(), digital, 1);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        assertEquals(new BigDecimal("45000.00"), order.getTotal().getAmount());
        assertEquals(fx.warehouseOf(seller), order.getWarehouse());
        assertEquals(6, fx.inventories.findById(inventory.getId()).get().getStock());
        assertTrue(fx.carts.findById(buyer.getCart().getId()).get().getItems().isEmpty());
    }

    @Test
    void cartBlocksMoreUnitsThanAvailableStock() {
        User user = buyer.getUser();
        assertThrows(InsufficientStockException.class, () -> add().addProduct(user, buyer.getCart(), physical, 11));
        assertTrue(fx.carts.findById(buyer.getCart().getId()).get().getItems().isEmpty());
        add().addProduct(user, buyer.getCart(), physical, 10);
        assertThrows(InsufficientStockException.class, () -> add().addProduct(user, buyer.getCart(), physical, 1));
        assertEquals(10, fx.carts.findById(buyer.getCart().getId()).get().getItems().get(0).getQuantity());
        UpdateCartItemQuantityService update = new UpdateCartItemQuantityService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadCart(), fx.cartPort, fx.productPort, new CartStockGuard(fx.inventoryPort));
        assertThrows(InsufficientStockException.class, () -> update.updateQuantity(user, buyer.getCart(), physical, 11));
        Product noStock = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        assertThrows(InsufficientStockException.class, () -> add().addProduct(user, buyer.getCart(), noStock, 1));
        add().addProduct(user, buyer.getCart(), digital, 50); // digital products have no stock limit
        assertEquals(2, fx.carts.findById(buyer.getCart().getId()).get().getItems().size());
    }

    @Test
    void createOrderValidations() {
        assertThrows(EmptyCartException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), null));
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 10);
        inventory.reserve(1); // someone else bought a unit after this cart was filled
        assertThrows(InsufficientStockException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), null));
        assertEquals(9, fx.inventories.findById(inventory.getId()).get().getStock());
        assertTrue(fx.orders.findAll().isEmpty());
        assertThrows(UnauthorizedOperationException.class,
                () -> createOrder().createOrder(fx.newBuyer().getUser(), buyer.getCart(), null));

        Warehouse empty = fx.warehouses.save(new Warehouse("Other", WarehouseType.MARKETPLACE, null));
        fx.carts.findById(buyer.getCart().getId()).get().updateQuantity(physical, 2);
        assertThrows(InsufficientStockException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), empty));
        empty.deactivate();
        assertThrows(InvalidWarehouseStatusException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), empty));

        buyer.setTradingStatus(TradingStatus.SUSPENDED);
        assertThrows(InvalidTradingStatusException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), null));
    }

    @Test
    void damagedStockCannotBeSold() {
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 1);
        inventory.markDamaged();
        assertThrows(InsufficientStockException.class, () -> createOrder().createOrder(buyer.getUser(), buyer.getCart(), null));
    }

    @Test
    void paymentOfDigitalOnlyOrderDeliversImmediately() {
        add().addProduct(buyer.getUser(), buyer.getCart(), digital, 1);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        assertNull(order.getWarehouse());
        pay().confirmPayment(buyer.getUser(), order);
        assertEquals(OrderStatus.FINISHED, fx.orders.findById(order.getId()).get().getStatus());
        assertEquals(1, fx.notifications.size());
    }

    @Test
    void paymentOfPhysicalOrderStopsAtPaid() {
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 1);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        pay().confirmPayment(buyer.getUser(), order);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertThrows(InvalidOrderStateException.class, () -> pay().confirmPayment(buyer.getUser(), order));
        assertThrows(UnauthorizedOperationException.class, () -> pay().confirmPayment(fx.newBuyer().getUser(), order));
    }

    @Test
    void cancellingReleasesStockAndCancelsInvoice() {
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 4);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        pay().confirmPayment(buyer.getUser(), order);
        Invoice invoice = new Invoice(order);
        invoice.generate(new BigDecimal("0.19"));
        fx.invoices.save(invoice);

        cancel().cancelOrder(buyer.getUser(), order);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(10, fx.inventories.findById(inventory.getId()).get().getStock());
        assertTrue(invoice.isCancelled());
        assertThrows(InvalidOrderStateException.class, () -> cancel().cancelOrder(buyer.getUser(), order));
    }

    @Test
    void shippedOrderCannotBeCancelled() {
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 1);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        pay().confirmPayment(buyer.getUser(), order);
        order.ship();
        assertThrows(InvalidOrderStateException.class, () -> cancel().cancelOrder(buyer.getUser(), order));
    }

    @Test
    void consultOrderVisibility() {
        ConsultOrderService consult = new ConsultOrderService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.orderPort, fx.buyerPort, fx.sellerPort);
        add().addProduct(buyer.getUser(), buyer.getCart(), physical, 1);
        Order order = createOrder().createOrder(buyer.getUser(), buyer.getCart(), null);
        Buyer other = fx.newBuyer();
        Seller otherSeller = fx.newSeller();

        assertSame(order, consult.consultOrder(buyer.getUser(), order));
        assertSame(order, consult.consultOrder(seller.getUser(), order));
        assertSame(order, consult.consultOrder(fx.logistics(), order));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultOrder(other.getUser(), order));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultOrder(otherSeller.getUser(), order));
        assertEquals(1, consult.consultMyOrders(buyer.getUser()).size());
        assertEquals(1, consult.consultMyOrders(seller.getUser()).size());
        assertEquals(0, consult.consultMyOrders(otherSeller.getUser()).size());
        assertEquals(1, consult.consultAllOrders(fx.supervisor()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllOrders(buyer.getUser()));
    }
}
