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
class BuyerSellerWarehouseServicesTest {

    private Fixture fx;
    private CreateUserAccountService create;

    @BeforeEach
    void setUp() {
        fx = new Fixture();
        create = new CreateUserAccountService(fx.userPort, fx.passwordPort);
    }

    @Test
    @DisplayName("Public buyer registration forces the BUYER role and creates profile + cart")
    void publicBuyerRegistration() {
        RegisterBuyerAccountService service = new RegisterBuyerAccountService(fx.validateStatus, fx.validateRole,
                fx.traceability, create, fx.buyerPort, fx.cartPort);
        User attempt = new User("carlos", "password123", "Carlos", "carlos@mail.com", "CC9", SystemRole.ADMINISTRATOR);
        Buyer buyer = service.registerBuyerAccount(attempt, "Calle 1");
        assertEquals(SystemRole.BUYER, buyer.getUser().getRole());
        assertNotNull(buyer.getCart());
        assertEquals(1, fx.carts.findAll().size());
        assertEquals(2, fx.operations.findAll().size());
    }

    @Test
    void publicRegistrationValidatesAddressBeforeStoring() {
        RegisterBuyerAccountService service = new RegisterBuyerAccountService(fx.validateStatus, fx.validateRole,
                fx.traceability, create, fx.buyerPort, fx.cartPort);
        User attempt = new User("carlos", "password123", "Carlos", "carlos@mail.com", "CC9", SystemRole.BUYER);
        assertThrows(InvalidBuyerException.class, () -> service.registerBuyerAccount(attempt, " "));
        assertTrue(fx.users.findAll().isEmpty());
    }

    @Test
    void buyerAddsAddressOnlyToOwnProfile() {
        AddBuyerAddressService service = new AddBuyerAddressService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.buyerPort);
        Buyer mine = fx.newBuyer();
        Buyer other = fx.newBuyer();
        service.addAddress(mine.getUser(), mine, "New address 5");
        assertEquals(1, fx.buyers.findById(mine.getId()).get().getAdditionalAddresses().size());
        assertThrows(UnauthorizedOperationException.class, () -> service.addAddress(mine.getUser(), other, "x y z"));
        assertThrows(UnauthorizedOperationException.class,
                () -> service.addAddress(fx.admin(), mine, "Admin cannot add this"));
    }

    @Test
    void buyerProfileCannotBeDuplicated() {
        RegisterBuyerService service = new RegisterBuyerService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.buyerPort, fx.cartPort);
        User user = fx.newUser(SystemRole.BUYER);
        assertNotNull(service.registerBuyer(user, "Street 9").getId());
        assertThrows(DuplicateResourceException.class, () -> service.registerBuyer(user, "Street 9"));
    }

    private RegisterSellerService sellerService() {
        return new RegisterSellerService(fx.validateStatus, fx.validateRole, fx.traceability, create, fx.userPort,
                fx.sellerPort, fx.warehousePort);
    }

    @Test
    void adminRegistersSellerWithFirstWarehouse() {
        User admin = fx.admin();
        User sellerUser = new User("tienda", "password123", "Tienda", "tienda@mail.com", "NIT1", SystemRole.BUYER);
        Seller seller = sellerService().registerSeller(admin, sellerUser, "Tienda S.A.S.", "Bodega Norte");
        assertEquals(SystemRole.SELLER, seller.getUser().getRole());
        assertEquals(1, seller.getWarehouses().size());
        assertEquals(seller, seller.getWarehouses().get(0).getOwner());
        assertThrows(UnauthorizedOperationException.class,
                () -> sellerService().registerSeller(fx.newBuyer().getUser(), sellerUser, "x", "y"));
    }

    @Test
    void existingUserMustBeSellerRoleAndNotAlreadyRegistered() {
        User admin = fx.admin();
        User buyerUser = fx.newUser(SystemRole.BUYER);
        assertThrows(InvalidSellerException.class,
                () -> sellerService().registerSeller(admin, buyerUser, "Shop", "Depot"));
        Seller existing = fx.newSeller();
        assertThrows(DuplicateResourceException.class,
                () -> sellerService().registerSeller(admin, existing.getUser(), "Shop", "Depot"));
    }

    @Test
    void sellerDeactivationSuspendsPublishedProducts() {
        DeactivateSellerService deactivate = new DeactivateSellerService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.sellerPort, fx.productPort);
        ReactivateSellerService reactivate = new ReactivateSellerService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.sellerPort);
        User admin = fx.admin();
        Seller seller = fx.newSeller();
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        deactivate.deactivate(admin, seller);
        assertFalse(fx.sellers.findById(seller.getId()).get().isActive());
        assertFalse(fx.products.findById(product.getId()).get().isPublished());
        assertThrows(InvalidSellerStatusException.class, () -> deactivate.deactivate(admin, seller));
        reactivate.reactivate(admin, seller);
        assertTrue(fx.sellers.findById(seller.getId()).get().isActive());
    }

    @Test
    void consultSellerIsRestrictedToOwnerOrStaff() {
        ConsultSellerService consult = new ConsultSellerService(fx.validateStatus, fx.validateRole, fx.traceability,
                fx.sellerPort);
        Seller a = fx.newSeller();
        Seller b = fx.newSeller();
        assertSame(a, consult.consultSeller(a.getUser(), a));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultSeller(a.getUser(), b));
        assertSame(b, consult.consultSeller(fx.supervisor(), b));
        assertSame(a, consult.consultMySeller(a.getUser()));
        assertEquals(2, consult.consultAllSellers(fx.admin()).size());
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllSellers(a.getUser()));
    }

    @Test
    void warehouseRegistrationRules() {
        RegisterWarehouseService register = new RegisterWarehouseService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.warehousePort, fx.sellerPort);
        User logistics = fx.logistics();
        Seller seller = fx.newSeller();
        Warehouse marketplace = register.registerWarehouse(logistics, "Central", WarehouseType.MARKETPLACE, null);
        assertNull(marketplace.getOwner());
        assertThrows(InvalidWarehouseException.class,
                () -> register.registerWarehouse(logistics, "Seller depot", WarehouseType.SELLER, null));
        assertThrows(InvalidWarehouseException.class,
                () -> register.registerWarehouse(logistics, "Central 2", WarehouseType.MARKETPLACE, seller));
        assertThrows(UnauthorizedOperationException.class,
                () -> register.registerWarehouse(seller.getUser(), "Mine", WarehouseType.SELLER, seller));
        seller.deactivate();
        assertThrows(InvalidSellerStatusException.class,
                () -> register.registerWarehouse(logistics, "Depot 2", WarehouseType.SELLER, seller));
    }

    @Test
    void warehouseWithStockCannotBeDeactivated() {
        DeactivateWarehouseService deactivate = new DeactivateWarehouseService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.warehousePort, fx.inventoryPort);
        Seller seller = fx.newSeller();
        Warehouse warehouse = fx.warehouseOf(seller);
        Product product = fx.newProduct(seller, ProductType.PHYSICAL, "1000", true);
        Inventory inventory = fx.newInventory(product, warehouse, 3);
        User logistics = fx.logistics();
        assertThrows(InvalidWarehouseStatusException.class, () -> deactivate.deactivateWarehouse(logistics, warehouse));
        inventory.adjust(0);
        fx.inventories.update(inventory);
        deactivate.deactivateWarehouse(logistics, warehouse);
        assertFalse(fx.warehouses.findById(warehouse.getId()).get().isActive());
    }

    @Test
    void sellersOnlySeeTheirOwnWarehouses() {
        ConsultWarehouseService consult = new ConsultWarehouseService(fx.validateStatus, fx.validateRole,
                fx.traceability, fx.warehousePort, fx.sellerPort);
        Seller a = fx.newSeller();
        Seller b = fx.newSeller();
        assertEquals(1, consult.consultAllWarehouses(a.getUser()).size());
        assertEquals(2, consult.consultAllWarehouses(fx.logistics()).size());
        assertThrows(UnauthorizedOperationException.class,
                () -> consult.consultWarehouse(a.getUser(), fx.warehouseOf(b)));
        assertThrows(UnauthorizedOperationException.class, () -> consult.consultAllWarehouses(fx.newBuyer().getUser()));
    }
}
