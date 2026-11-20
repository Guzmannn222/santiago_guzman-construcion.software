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

/** Pure domain rules: no ports, no services. */
class ModelRulesTest {

    private final Fixture fx = new Fixture();

    @Test
    @DisplayName("User: blocked users lose access and tokens are invalidated")
    void userBlockBumpsTokenVersion() {
        User user = fx.newUser(SystemRole.BUYER);
        int version = user.getAuthTokenVersion().intValue();
        user.block();
        assertFalse(user.validateAccess());
        assertEquals(version + 1, user.getAuthTokenVersion().intValue());
        user.activate();
        assertTrue(user.validateAccess());
    }

    @Test
    void userPasswordFormatIsValidated() {
        assertThrows(InvalidUserException.class, () -> User.validatePasswordFormat("short"));
        assertDoesNotThrow(() -> User.validatePasswordFormat("long-enough-1"));
    }

    @Test
    void moneyKeepsScaleAndRejectsMixedCurrencies() {
        Money a = Fixture.cop("100");
        assertEquals(new BigDecimal("100.00"), a.getAmount());
        assertEquals(new BigDecimal("300.00"), a.multiply(3).getAmount());
        Money usd = Money.of(BigDecimal.TEN, application.domain.valueobjects.Currency.USD);
        assertThrows(CurrencyMismatchException.class, () -> a.add(usd));
        assertThrows(InvalidMoneyException.class, () -> Money.of(null, application.domain.valueobjects.Currency.COP));
    }

    @Test
    void catalogsResolveByCode() {
        assertSame(SystemRole.BUYER, SystemRole.fromCode("BUYER"));
        assertThrows(IllegalArgumentException.class, () -> SystemRole.fromCode("NOPE"));
    }

    @Test
    void productPriceMustBePositiveAndStatusFollowsMatrix() {
        Seller seller = fx.newSeller();
        assertThrows(InvalidPriceException.class,
                () -> new Product(seller, "x", "d", Fixture.cop("0"), ProductType.PHYSICAL));
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", false);
        assertFalse(product.isPublished());
        product.publish();
        assertTrue(product.isPublished());
        product.discontinue();
        assertThrows(InvalidProductStatusException.class, product::publish);
    }

    @Test
    void inventoryNeverGoesNegativeAndDamagedCannotBeReserved() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Inventory inventory = new Inventory(product, fx.warehouseOf(seller), 10);
        inventory.reserve(4);
        assertEquals(6, inventory.getStock());
        assertThrows(InsufficientStockException.class, () -> inventory.reserve(7));
        assertThrows(InvalidQuantityException.class, () -> inventory.adjust(-1));
        inventory.markDamaged();
        assertFalse(inventory.canFulfill(1));
        assertThrows(DamagedInventoryException.class, () -> inventory.reserve(1));
    }

    @Test
    void digitalProductsHaveNoInventory() {
        Seller seller = fx.newSeller();
        Product digital = fx.newProduct(seller, ProductType.DIGITAL, "1000", true);
        assertThrows(InvalidInventoryException.class, () -> new Inventory(digital, fx.warehouseOf(seller), 1));
    }

    @Test
    void cartLimitsQuantityAndRequiresItemsToConfirm() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "500", true);
        Cart cart = new Cart(fx.newBuyer());
        assertThrows(EmptyCartException.class, cart::confirm);
        cart.addProduct(product, 2);
        assertEquals(new BigDecimal("1000.00"), cart.getTotal().getAmount());
        assertThrows(InvalidCartException.class, () -> cart.addProduct(product, Cart.MAX_QUANTITY_PER_ITEM));
        assertThrows(InvalidCartException.class, () -> cart.addProduct(product, 0));
        cart.removeProduct(product);
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void cartRejectsUnpublishedProducts() {
        Seller seller = fx.newSeller();
        Product draft = fx.newProduct(seller, ProductType.PHYSICAL, "500", false);
        Cart cart = new Cart(fx.newBuyer());
        assertThrows(DomainException.class, () -> cart.addProduct(draft, 1));
    }

    @Test
    void orderStatusFollowsMatrixAndFinishedIsFinal() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "500", true);
        Order order = new Order(fx.newBuyer(), fx.warehouseOf(seller));
        order.addItem(new OrderItem(product, 2, product.getPrice()));
        assertEquals(new BigDecimal("1000.00"), order.getTotal().getAmount());
        assertThrows(InvalidOrderStateException.class, order::ship);
        order.confirmPayment();
        order.ship();
        order.deliver();
        order.finish();
        assertFalse(order.canModify());
        assertThrows(InvalidOrderStateException.class, order::cancel);
        assertThrows(InvalidOrderStateException.class, order::confirmPayment);
    }

    @Test
    void invoiceNeedsPaidOrderAndValidRate() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Order order = new Order(fx.newBuyer(), fx.warehouseOf(seller));
        order.addItem(new OrderItem(product, 1, product.getPrice()));
        Invoice invoice = new Invoice(order);
        assertThrows(InvalidInvoiceStatusException.class, () -> invoice.generate(new BigDecimal("0.19")));
        order.confirmPayment();
        assertThrows(InvalidInvoiceException.class, () -> invoice.generate(new BigDecimal("1.5")));
        invoice.generate(new BigDecimal("0.19"));
        assertEquals(new BigDecimal("190.00"), invoice.getTax().getAmount());
        assertEquals(new BigDecimal("1190.00"), invoice.getTotal().getAmount());
        invoice.cancel();
        assertThrows(InvalidInvoiceStatusException.class, invoice::cancel);
    }

    @Test
    void returnRequiresDeliveredOrderAndRefundCannotExceedTotal() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Order order = new Order(fx.newBuyer(), fx.warehouseOf(seller));
        order.addItem(new OrderItem(product, 1, product.getPrice()));
        assertThrows(InvalidReturnStatusException.class, () -> new Return(order, "broken"));
        order.confirmPayment();
        order.ship();
        order.deliver();
        Return productReturn = new Return(order, "broken");
        assertFalse(productReturn.isRefundable());
        productReturn.approve();
        assertTrue(productReturn.isRefundable());
        assertThrows(InvalidRefundException.class, () -> new Refund(productReturn, Fixture.cop("1001")));
        Refund refund = new Refund(productReturn, Fixture.cop("1000"));
        refund.process();
        refund.complete();
        assertThrows(InvalidRefundStatusException.class, refund::reject);
    }

    @Test
    void buyerAddressesAreUniqueAndLimited() {
        Buyer buyer = fx.newBuyer();
        assertThrows(InvalidBuyerException.class, () -> buyer.addAddress("Street 1 #2-3"));
        for (int i = 0; i < Buyer.MAX_ADDITIONAL_ADDRESSES; i++) {
            buyer.addAddress("Other " + i);
        }
        assertThrows(InvalidBuyerException.class, () -> buyer.addAddress("One more"));
    }
}
