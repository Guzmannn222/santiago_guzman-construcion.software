package application.infrastructure.seed;

import application.domain.models.Buyer;
import application.domain.models.Cart;
import application.domain.models.Inventory;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Variant;
import application.domain.models.Warehouse;
import application.domain.ports.out.BuyerRepositoryPort;
import application.domain.ports.out.CartRepositoryPort;
import application.domain.ports.out.InventoryRepositoryPort;
import application.domain.ports.out.PasswordServicePort;
import application.domain.ports.out.ProductRepositoryPort;
import application.domain.ports.out.SellerRepositoryPort;
import application.domain.ports.out.UserRepositoryPort;
import application.domain.ports.out.WarehouseRepositoryPort;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the database so every role and the main flows work right after startup: one user per
 * system role (admin, supervisor, logistics operator, seller, buyer), the seller's warehouse,
 * a published physical product with variants and stock, a digital product, and the buyer's cart.
 *
 * <p>Idempotent by username / identifying fields. Shared password for every seed user:
 * {@code Password123*}. Passwords are hashed through {@link PasswordServicePort}.
 */
@Component
@Order(1)
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);
    private static final String PASSWORD = "Password123*";

    private final UserRepositoryPort userRepositoryPort;
    private final BuyerRepositoryPort buyerRepositoryPort;
    private final SellerRepositoryPort sellerRepositoryPort;
    private final WarehouseRepositoryPort warehouseRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;
    private final InventoryRepositoryPort inventoryRepositoryPort;
    private final CartRepositoryPort cartRepositoryPort;
    private final PasswordServicePort passwordServicePort;

    public DatabaseSeeder(UserRepositoryPort userRepositoryPort,
                          BuyerRepositoryPort buyerRepositoryPort,
                          SellerRepositoryPort sellerRepositoryPort,
                          WarehouseRepositoryPort warehouseRepositoryPort,
                          ProductRepositoryPort productRepositoryPort,
                          InventoryRepositoryPort inventoryRepositoryPort,
                          CartRepositoryPort cartRepositoryPort,
                          PasswordServicePort passwordServicePort) {
        this.userRepositoryPort = userRepositoryPort;
        this.buyerRepositoryPort = buyerRepositoryPort;
        this.sellerRepositoryPort = sellerRepositoryPort;
        this.warehouseRepositoryPort = warehouseRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
        this.inventoryRepositoryPort = inventoryRepositoryPort;
        this.cartRepositoryPort = cartRepositoryPort;
        this.passwordServicePort = passwordServicePort;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Seeding database (all roles and sample marketplace data)...");
        try {
            seedUser("admin", "Admin Nexus", "admin@nexusmarket.com", "1000000001", SystemRole.ADMINISTRATOR);
            seedUser("supervisor", "Sofia Supervisor", "supervisor@nexusmarket.com", "1000000002", SystemRole.SUPERVISOR);
            seedUser("logistics", "Luis Logistica", "logistics@nexusmarket.com", "1000000003", SystemRole.LOGISTICS_OPERATOR);

            User sellerUser = seedUser("seller", "Tienda Andina", "seller@nexusmarket.com", "1000000004", SystemRole.SELLER);
            User buyerUser = seedUser("buyer", "Bruno Comprador", "buyer@nexusmarket.com", "1000000005", SystemRole.BUYER);

            Seller seller = sellerRepositoryPort.findByUserId(sellerUser.getId()).orElseGet(() ->
                    sellerRepositoryPort.save(new Seller(sellerUser, "Tienda Andina S.A.S. - tecnologia y accesorios")));

            Warehouse warehouse = warehouseRepositoryPort.findByOwnerId(seller.getId()).stream().findFirst()
                    .orElseGet(() -> warehouseRepositoryPort.save(
                            new Warehouse("Bogota - Zona Industrial", WarehouseType.SELLER, seller)));

            Buyer buyer = buyerRepositoryPort.findByUserId(buyerUser.getId()).orElseGet(() ->
                    buyerRepositoryPort.save(new Buyer(buyerUser, "Calle 100 # 15-20, Bogota")));
            if (cartRepositoryPort.findByBuyerId(buyer.getId()).isEmpty()) {
                cartRepositoryPort.save(new Cart(buyer));
            }

            if (productRepositoryPort.findBySellerId(seller.getId()).isEmpty()) {
                Product keyboard = new Product(seller, "Teclado mecanico",
                        "Teclado mecanico 75% con switches rojos",
                        Money.of(new BigDecimal("250000"), Currency.COP), ProductType.PHYSICAL);
                keyboard.addVariant(new Variant("Color", "Negro"));
                keyboard.addVariant(new Variant("Idioma", "Espanol"));
                keyboard.publish();
                keyboard = productRepositoryPort.save(keyboard);

                Inventory inventory = new Inventory(keyboard, warehouse, 50);
                inventoryRepositoryPort.save(inventory);

                Product ebook = new Product(seller, "Curso de Java (e-book)",
                        "Guia digital de arquitectura hexagonal con Java",
                        Money.of(new BigDecimal("60000"), Currency.COP), ProductType.DIGITAL);
                ebook.publish();
                productRepositoryPort.save(ebook);
            }
            log.info("Seeding finished. Users: admin, supervisor, logistics, seller, buyer (password {}).", PASSWORD);
        } catch (RuntimeException ex) {
            log.error("Database seeding failed: {}", ex.getMessage());
            throw ex;
        }
    }

    private User seedUser(String username, String name, String email, String identityDocument, SystemRole role) {
        return userRepositoryPort.findByUsername(username).orElseGet(() -> userRepositoryPort.save(
                new User(username, passwordServicePort.encrypt(PASSWORD), name, email, identityDocument, role)));
    }
}
