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
class ProductInventoryServicesTest {

    private Fixture fx;
    private LoadManagedProductService loadProduct;
    private LoadManagedInventoryService loadInventory;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        loadProduct = new LoadManagedProductService(fx.productPort);
        loadInventory = new LoadManagedInventoryService(fx.inventoryPort);
    }

    private CreateProductService create() {
        return new CreateProductService(fx.validateStatus, fx.validateRole, fx.traceability, fx.sellerPort, fx.productPort);
    }

    @Test
    void sellerCreatesProductSuspendedInOwnCatalog() {
        Seller seller = fx.newSeller();
        Product product = create().createProduct(seller.getUser(), seller, "Lamp", "Nice", Fixture.cop("25000"), ProductType.PHYSICAL);
        assertEquals(ProductStatus.SUSPENDED, product.getStatus());
        assertEquals(seller, product.getSeller());
        assertEquals(OperationType.PRODUCT_CREATION, fx.audits.get(0).getOperationType());
    }

    @Test
    void createProductValidations() {
        Seller seller = fx.newSeller();
        Seller other = fx.newSeller();
        User actor = seller.getUser();
        assertThrows(InvalidPriceException.class,
                () -> create().createProduct(actor, seller, "Lamp", "d", Fixture.cop("0"), ProductType.PHYSICAL));
        assertThrows(InvalidProductException.class,
                () -> create().createProduct(actor, seller, " ", "d", Fixture.cop("10"), ProductType.PHYSICAL));
        assertThrows(UnauthorizedOperationException.class,
                () -> create().createProduct(actor, other, "Lamp", "d", Fixture.cop("10"), ProductType.PHYSICAL));
        assertThrows(UnauthorizedOperationException.class,
                () -> create().createProduct(fx.admin(), seller, "Lamp", "d", Fixture.cop("10"), ProductType.PHYSICAL));
        seller.deactivate();
        assertThrows(InvalidSellerStatusException.class,
                () -> create().createProduct(actor, seller, "Lamp", "d", Fixture.cop("10"), ProductType.PHYSICAL));
    }

    @Test
    void lifecycleAndOwnership() {
        PublishProductService publish = new PublishProductService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadProduct, fx.productPort);
        SuspendProductService suspend = new SuspendProductService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadProduct, fx.productPort);
        DiscontinueProductService discontinue = new DiscontinueProductService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadProduct, fx.productPort);
        Seller seller = fx.newSeller();
        Seller intruder = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", false);

        assertThrows(UnauthorizedOperationException.class, () -> publish.publish(intruder.getUser(), product));
        publish.publish(seller.getUser(), product);
        assertTrue(fx.products.findById(product.getId()).get().isPublished());
        suspend.suspend(fx.admin(), product);
        assertFalse(fx.products.findById(product.getId()).get().isPublished());
        discontinue.discontinue(seller.getUser(), product);
        assertThrows(InvalidProductStatusException.class, () -> publish.publish(seller.getUser(), product));
        assertThrows(EntityNotFoundException.class, () -> publish.publish(seller.getUser(), new Product()));
    }

    @Test
    void priceUpdateAndVariants() {
        UpdateProductPriceService price = new UpdateProductPriceService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadProduct, fx.productPort);
        AddProductVariantService variant = new AddProductVariantService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadProduct, fx.productPort);
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        price.updatePrice(seller.getUser(), product, Fixture.cop("1500"));
        assertEquals(new BigDecimal("1500.00"), fx.products.findById(product.getId()).get().getPrice().getAmount());
        assertThrows(InvalidPriceException.class, () -> price.updatePrice(seller.getUser(), product, Fixture.cop("0")));
        assertThrows(InvalidMoneyException.class, () -> Fixture.cop("-1"));
        variant.addVariant(seller.getUser(), product, "Color", "Red");
        assertEquals(1, fx.products.findById(product.getId()).get().getVariants().size());
        assertThrows(DomainValidationException.class, () -> variant.addVariant(seller.getUser(), product, " ", "Red"));
    }

    @Test
    void consultProductVisibility() {
        ConsultProductService consult = new ConsultProductService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.productPort, fx.sellerPort);
        Seller seller = fx.newSeller();
        Product draft = fx.newProduct(seller, ProductType.PHYSICAL, "1000", false);
        Product live = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        User buyer = fx.newBuyer().getUser();
        assertThrows(EntityNotFoundException.class, () -> consult.consultProduct(buyer, draft));
        assertSame(live, consult.consultProduct(buyer, live));
        assertSame(draft, consult.consultProduct(seller.getUser(), draft));
        assertSame(draft, consult.consultProduct(fx.supervisor(), draft));
        assertEquals(1, consult.consultPublishedProducts(buyer).size());
        assertEquals(2, consult.consultMyProducts(seller.getUser()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllProducts(buyer));
    }

    private OpenInventoryService open() {
        return new OpenInventoryService(fx.validateStatus, fx.validateRole, fx.traceability, fx.productPort,
                fx.warehousePort, fx.inventoryPort, fx.configPort);
    }

    @Test
    void openInventoryRules() {
        Seller seller = fx.newSeller();
        Seller other = fx.newSeller();
        Product physical = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Product digital = fx.newProduct(seller, ProductType.DIGITAL, "1000", true);
        Warehouse warehouse = fx.warehouseOf(seller);

        Inventory inventory = open().openInventory(seller.getUser(), physical, warehouse, 10);
        assertEquals(10, inventory.getStock());
        assertEquals(InventoryStatus.AVAILABLE, inventory.getStatus());
        assertEquals(1, inventory.getMovements().size());
        assertThrows(DuplicateResourceException.class, () -> open().openInventory(seller.getUser(), physical, warehouse, 1));
        assertThrows(InvalidInventoryException.class, () -> open().openInventory(seller.getUser(), digital, warehouse, 1));
        assertThrows(InvalidQuantityException.class,
                () -> open().openInventory(fx.logistics(), fx.newProduct(seller, ProductType.PHYSICAL, "5", true), warehouse, -1));
        assertThrows(UnauthorizedOperationException.class,
                () -> open().openInventory(other.getUser(), physical, fx.warehouseOf(other), 1));
        warehouse.deactivate();
        Product p2 = fx.newProduct(seller, ProductType.PHYSICAL, "5", true);
        assertThrows(InvalidWarehouseStatusException.class, () -> open().openInventory(seller.getUser(), p2, warehouse, 1));
    }

    @Test
    void lowStockStatusUsesConfiguredThreshold() {
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Inventory inventory = open().openInventory(seller.getUser(), product, fx.warehouseOf(seller), 3);
        assertEquals(InventoryStatus.LOW_STOCK, inventory.getStatus());
    }

    @Test
    void stockMovementsAreRecordedAndValidated() {
        ReceiveStockService receive = new ReceiveStockService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadInventory, fx.inventoryPort);
        ReserveStockService reserve = new ReserveStockService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadInventory, fx.inventoryPort);
        ReturnStockService back = new ReturnStockService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadInventory, fx.inventoryPort);
        AdjustStockService adjust = new AdjustStockService(fx.validateStatus, fx.validateRole, fx.traceability,
                loadInventory, fx.inventoryPort);
        MarkInventoryDamagedService damaged = new MarkInventoryDamagedService(fx.validateStatus, fx.validateRole,
                fx.traceability, loadInventory, fx.inventoryPort);
        Seller seller = fx.newSeller();
        Seller intruder = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Inventory inventory = fx.newInventory(product, fx.warehouseOf(seller), 10);
        User sellerUser = seller.getUser();
        User logistics = fx.logistics();

        receive.receiveStock(sellerUser, inventory, 5, "restock");
        assertEquals(15, fx.inventories.findById(inventory.getId()).get().getStock());
        assertThrows(InvalidQuantityException.class, () -> receive.receiveStock(sellerUser, inventory, 0, "x"));
        assertThrows(UnauthorizedOperationException.class, () -> receive.receiveStock(intruder.getUser(), inventory, 1, "x"));
        assertThrows(UnauthorizedOperationException.class, () -> receive.receiveStock(fx.newBuyer().getUser(), inventory, 1, "x"));

        reserve.reserveStock(logistics, inventory, 4, "manual");
        assertEquals(11, inventory.getStock());
        assertThrows(InsufficientStockException.class, () -> reserve.reserveStock(logistics, inventory, 99, "x"));
        assertThrows(UnauthorizedOperationException.class, () -> reserve.reserveStock(sellerUser, inventory, 1, "x"));

        back.returnStock(logistics, inventory, 2, "customer return");
        assertEquals(13, inventory.getStock());

        assertThrows(InvalidInventoryException.class, () -> adjust.adjustStock(sellerUser, inventory, 1, " "));
        assertThrows(InvalidQuantityException.class, () -> adjust.adjustStock(sellerUser, inventory, -3, "count"));
        adjust.adjustStock(sellerUser, inventory, 7, "physical count");
        assertEquals(7, inventory.getStock());

        damaged.markDamaged(sellerUser, inventory);
        assertEquals(InventoryStatus.DAMAGED, inventory.getStatus());
        assertThrows(DamagedInventoryException.class, () -> reserve.reserveStock(logistics, inventory, 1, "x"));
        assertThrows(InvalidInventoryException.class, () -> damaged.markDamaged(sellerUser, inventory));
    }

    @Test
    void checkAvailabilityAndConsultInventory() {
        CheckStockAvailabilityService check = new CheckStockAvailabilityService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.inventoryPort);
        ConsultInventoryService consult = new ConsultInventoryService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.inventoryPort, fx.sellerPort, fx.productPort, fx.warehousePort);
        Seller seller = fx.newSeller();
        Seller other = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Inventory inventory = fx.newInventory(product, fx.warehouseOf(seller), 5);
        User buyer = fx.newBuyer().getUser();
        assertTrue(check.checkAvailability(buyer, inventory, 5));
        assertFalse(check.checkAvailability(buyer, inventory, 6));
        assertThrows(InvalidQuantityException.class, () -> check.checkAvailability(buyer, inventory, 0));

        assertSame(inventory, consult.consultInventory(seller.getUser(), inventory));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultInventory(other.getUser(), inventory));
        assertSame(inventory, consult.consultByProductAndWarehouse(fx.logistics(), product, fx.warehouseOf(seller)));
        assertEquals(1, consult.consultMyInventory(seller.getUser()).size());
        assertEquals(0, consult.consultMyInventory(other.getUser()).size());
        assertEquals(1, consult.consultAllInventory(fx.supervisor()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllInventory(seller.getUser()));
    }
}
