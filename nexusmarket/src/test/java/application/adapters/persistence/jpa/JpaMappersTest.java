package application.adapters.persistence.jpa;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.adapters.persistence.jpa.entities.BuyerJpaEntity;
import application.adapters.persistence.jpa.entities.CartItemJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryJpaEntity;
import application.adapters.persistence.jpa.entities.InventoryMovementJpaEntity;
import application.adapters.persistence.jpa.entities.OperationJpaEntity;
import application.adapters.persistence.jpa.entities.OrderItemJpaEntity;
import application.adapters.persistence.jpa.entities.OrderJpaEntity;
import application.adapters.persistence.jpa.entities.ProductJpaEntity;
import application.adapters.persistence.jpa.entities.ProductVariantJpaEntity;
import application.adapters.persistence.jpa.entities.UserJpaEntity;
import application.adapters.persistence.jpa.mappers.BuyerJpaMapper;
import application.adapters.persistence.jpa.mappers.CartJpaMapper;
import application.adapters.persistence.jpa.mappers.InventoryJpaMapper;
import application.adapters.persistence.jpa.mappers.MoneyJpaMapper;
import application.adapters.persistence.jpa.mappers.OperationJpaMapper;
import application.adapters.persistence.jpa.mappers.OrderJpaMapper;
import application.adapters.persistence.jpa.mappers.ProductJpaMapper;
import application.adapters.persistence.jpa.mappers.UserJpaMapper;
import application.domain.models.Buyer;
import application.domain.models.CartItem;
import application.domain.models.Inventory;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.models.Variant;
import application.domain.models.Warehouse;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.OrderStatus;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import application.domain.valueobjects.UserStatus;
import application.domain.valueobjects.WarehouseType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JpaMappersTest {

    private static User user(Long id, SystemRole role) {
        User u = new User("user" + id, "hash", "Name " + id, "u" + id + "@mail.com", "DOC" + id, role);
        u.setId(id);
        return u;
    }

    private static Seller seller() {
        Seller s = new Seller(user(2L, SystemRole.SELLER), "Business");
        s.setId(20L);
        return s;
    }

    private static Product product(Long id) {
        Product p = new Product(seller(), "Keyboard", "Mechanical",
                Money.of(new BigDecimal("250000"), Currency.COP), ProductType.PHYSICAL);
        p.setId(id);
        return p;
    }

    @Test
    @DisplayName("Money is flattened into amount + currency and rebuilt, null-safe")
    void moneyRoundTrip() {
        Money money = Money.of(new BigDecimal("10.5"), Currency.USD);
        assertEquals(new BigDecimal("10.50"), MoneyJpaMapper.amount(money));
        assertEquals("USD", MoneyJpaMapper.currency(money));
        assertEquals(money, MoneyJpaMapper.toMoney(new BigDecimal("10.50"), "USD"));
        assertNull(MoneyJpaMapper.amount(null));
        assertNull(MoneyJpaMapper.currency(null));
        assertNull(MoneyJpaMapper.toMoney(null, "USD"));
        assertNull(MoneyJpaMapper.toMoney(BigDecimal.ONE, null));
    }

    @Test
    @DisplayName("User maps catalogs by code and keeps the token version")
    void userRoundTrip() {
        User domain = user(1L, SystemRole.ADMINISTRATOR);
        domain.setAuthTokenVersion(4);
        UserJpaEntity entity = UserJpaMapper.toEntity(domain);
        assertEquals("ADMINISTRATOR", entity.getRole());
        assertEquals("ACTIVE", entity.getStatus());
        User back = UserJpaMapper.toDomain(entity);
        assertEquals((Object) 1L, back.getId());
        assertEquals("user1", back.getUsername());
        assertEquals(SystemRole.ADMINISTRATOR, back.getRole());
        assertEquals(UserStatus.ACTIVE, back.getStatus());
        assertEquals((Object) 4, back.getAuthTokenVersion());
        assertNull(UserJpaMapper.toEntity(null));
        assertNull(UserJpaMapper.toDomain(null));
    }

    @Test
    @DisplayName("Buyer additional addresses keep their order")
    void buyerRoundTrip() {
        Buyer buyer = new Buyer(user(5L, SystemRole.BUYER), "Main street 1");
        buyer.setId(50L);
        buyer.addAddress("Second street 2");
        buyer.addAddress("Third street 3");
        BuyerJpaEntity entity = BuyerJpaMapper.toEntity(buyer);
        assertEquals((Object) 5L, entity.getUserId());
        assertEquals("ACTIVE", entity.getTradingStatus());
        var rows = BuyerJpaMapper.toAddressEntities(50L, buyer.getAdditionalAddresses());
        assertEquals((Object) 2, rows.size());
        assertEquals((Object) 0, rows.get(0).getPosition());
        assertEquals((Object) 1, rows.get(1).getPosition());
        Buyer back = BuyerJpaMapper.toDomain(entity, buyer.getUser(), rows);
        assertEquals(List.of("Second street 2", "Third street 3"), back.getAdditionalAddresses());
        assertEquals("Main street 1", back.getPrimaryAddress());
    }

    @Test
    @DisplayName("Product maps price columns, status and variants")
    void productRoundTrip() {
        Product product = product(7L);
        product.addVariant(new Variant("Color", "Black"));
        product.publish();
        ProductJpaEntity entity = ProductJpaMapper.toEntity(product);
        assertEquals(new BigDecimal("250000.00"), entity.getPriceAmount());
        assertEquals("COP", entity.getPriceCurrency());
        assertEquals("PUBLISHED", entity.getStatus());
        assertEquals((Object) 20L, entity.getSellerId());
        List<ProductVariantJpaEntity> rows = ProductJpaMapper.toVariantEntities(7L, product.getVariants());
        assertEquals((Object) 1, rows.size());
        assertEquals((Object) 7L, rows.get(0).getProductId());
        Product back = ProductJpaMapper.toDomain(entity, seller(), rows);
        assertEquals(Money.of(new BigDecimal("250000"), Currency.COP), back.getPrice());
        assertEquals(ProductStatus.PUBLISHED, back.getStatus());
        assertEquals(ProductType.PHYSICAL, back.getType());
        assertEquals((Object) 1, back.getVariants().size());
        assertEquals("Black", back.getVariants().get(0).getValue());
    }

    @Test
    @DisplayName("Inventory keeps stock, threshold and movement history")
    void inventoryRoundTrip() {
        Warehouse warehouse = new Warehouse("Bogota", WarehouseType.SELLER, seller());
        warehouse.setId(30L);
        Product product = product(7L);
        Inventory inventory = new Inventory(product, warehouse, 10);
        inventory.setId(70L);
        inventory.setLowStockThreshold(3);
        inventory.inbound(5);
        inventory.recordMovement(new application.domain.models.InventoryMovement(
                application.domain.valueobjects.InventoryMovementType.INBOUND, 5, "restock"));
        InventoryJpaEntity entity = InventoryJpaMapper.toEntity(inventory);
        assertEquals((Object) 15, entity.getStock());
        assertEquals((Object) 3, entity.getLowStockThreshold());
        assertEquals((Object) 7L, entity.getProductId());
        assertEquals((Object) 30L, entity.getWarehouseId());
        List<InventoryMovementJpaEntity> rows = InventoryJpaMapper.toMovementEntities(70L, inventory.getMovements());
        assertFalse(rows.isEmpty());
        Inventory back = InventoryJpaMapper.toDomain(entity, product, warehouse, rows);
        assertEquals((Object) 15, back.getStock());
        assertEquals((Object) 3, back.getLowStockThreshold());
        assertEquals(inventory.getStatus(), back.getStatus());
        assertEquals(inventory.getMovements().size(), back.getMovements().size());
        assertEquals(inventory.getMovements().get(0).getType(), back.getMovements().get(0).getType());
    }

    @Test
    @DisplayName("Cart items keep product reference, quantity and unit price")
    void cartItemRoundTrip() {
        Product product = product(7L);
        CartItem item = new CartItem(product, 3, product.getPrice());
        List<CartItemJpaEntity> rows = CartJpaMapper.toItemEntities(9L, List.of(item));
        assertEquals((Object) 1, rows.size());
        assertEquals((Object) 7L, rows.get(0).getProductId());
        assertEquals((Object) 3, rows.get(0).getQuantity());
        CartItem back = CartJpaMapper.toItemDomain(rows.get(0), product);
        assertEquals((Object) 3, back.getQuantity());
        assertEquals(product.getPrice(), back.getUnitPrice());
        assertEquals(new BigDecimal("750000.00"), back.getLineTotal().getAmount());
    }

    @Test
    @DisplayName("Order keeps status, nullable warehouse and item money columns")
    void orderRoundTrip() {
        Buyer buyer = new Buyer(user(5L, SystemRole.BUYER), "Main street 1");
        buyer.setId(50L);
        Product product = product(7L);
        Order order = new Order(buyer, null);
        order.setId(100L);
        order.addItem(new OrderItem(product, 2, product.getPrice()));
        OrderJpaEntity entity = OrderJpaMapper.toEntity(order);
        assertNull(entity.getWarehouseId());
        assertEquals("PENDING_PAYMENT", entity.getStatus());
        assertEquals(new BigDecimal("500000.00"), entity.getTotalAmount());
        List<OrderItemJpaEntity> rows = OrderJpaMapper.toItemEntities(100L, order.getItems());
        assertEquals(new BigDecimal("500000.00"), rows.get(0).getSubtotalAmount());
        OrderItem itemBack = OrderJpaMapper.toItemDomain(rows.get(0), product);
        Order back = OrderJpaMapper.toDomain(entity, buyer, null, List.of(itemBack));
        assertEquals(OrderStatus.PENDING_PAYMENT, back.getStatus());
        assertNull(back.getWarehouse());
        assertEquals(order.getTotal(), back.getTotal());
        assertEquals((Object) 1, back.getItems().size());
        assertEquals((Object) 2, back.getItems().get(0).getQuantity());
    }

    @Test
    @DisplayName("Operation flattens the affected entity reference and rebuilds it")
    void operationRoundTrip() {
        Operation operation = new Operation();
        operation.setOperationId(1L);
        operation.setOperationType(OperationType.ORDER_CREATION);
        operation.setExecutionDate(LocalDateTime.of(2026, 1, 2, 3, 4));
        operation.setPerformedBy(user(5L, SystemRole.BUYER));
        operation.setAffectedEntity(EntityReference.of(EntityType.ORDER, 100L));
        OperationJpaEntity entity = OperationJpaMapper.toEntity(operation);
        assertEquals("ORDER_CREATION", entity.getOperationType());
        assertEquals("ORDER", entity.getAffectedEntityType());
        assertEquals("100", entity.getAffectedEntityIdentifier());
        assertEquals((Object) 5L, entity.getPerformedByUserId());
        Operation back = OperationJpaMapper.toDomain(entity, operation.getPerformedBy());
        assertEquals(OperationType.ORDER_CREATION, back.getOperationType());
        assertEquals(EntityReference.of(EntityType.ORDER, 100L), back.getAffectedEntity());
        assertNotNull(back.getPerformedBy());
        assertTrue(back.getExecutionDate().equals(operation.getExecutionDate()));
    }
}
