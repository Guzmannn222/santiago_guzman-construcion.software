package application.adapters.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import application.adapters.rest.dtos.requests.LoginRequestDTO;
import application.adapters.rest.dtos.requests.RegisterBuyerRequestDTO;
import application.adapters.rest.dtos.requests.RegisterUserRequestDTO;
import application.adapters.rest.dtos.responses.MoneyResponseDTO;
import application.adapters.rest.dtos.responses.OperationResponseDTO;
import application.adapters.rest.dtos.responses.OrderResponseDTO;
import application.adapters.rest.dtos.responses.ProductResponseDTO;
import application.adapters.rest.dtos.responses.UserResponseDTO;
import application.adapters.rest.mappers.EntityStubs;
import application.adapters.rest.mappers.MarketRestMapper;
import application.adapters.rest.mappers.MoneyRestMapper;
import application.adapters.rest.mappers.UserRestMapper;
import application.domain.models.Operation;
import application.domain.models.Order;
import application.domain.models.OrderItem;
import application.domain.models.Product;
import application.domain.models.Seller;
import application.domain.models.User;
import application.domain.valueobjects.Currency;
import application.domain.valueobjects.EntityReference;
import application.domain.valueobjects.EntityType;
import application.domain.valueobjects.Money;
import application.domain.valueobjects.OperationType;
import application.domain.valueobjects.ProductStatus;
import application.domain.valueobjects.ProductType;
import application.domain.valueobjects.SystemRole;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RestMappersTest {

    private User user() {
        User user = new User();
        user.setId(7L);
        user.setUsername("ana");
        user.setPassword("$2a$10$hashhashhash");
        user.setIdentityDocument("1001");
        user.setName("Ana");
        user.setEmail("ana@example.com");
        user.setRole(SystemRole.BUYER);
        return user;
    }

    @Test
    @DisplayName("user response never exposes the password hash and carries catalog codes")
    void userResponse() {
        UserResponseDTO dto = UserRestMapper.toResponseDTO(user());
        assertEquals(Long.valueOf(7L), dto.getId());
        assertEquals("BUYER", dto.getRole());
        assertEquals("ACTIVE", dto.getStatus());
        assertNull(UserRestMapper.toResponseDTO(null));
        for (java.lang.reflect.Field field : UserResponseDTO.class.getDeclaredFields()) {
            assertTrue(!field.getName().toLowerCase().contains("password"));
        }
    }

    @Test
    @DisplayName("request DTOs map to domain users with the right fixed or requested role")
    void requestToDomain() {
        LoginRequestDTO login = new LoginRequestDTO();
        login.setUsername("ana");
        login.setPassword("secret");
        assertEquals("ana", UserRestMapper.toDomain(login).getUsername());

        RegisterBuyerRequestDTO buyer = new RegisterBuyerRequestDTO();
        buyer.setUsername("ana");
        buyer.setPassword("Secret123");
        assertEquals(SystemRole.BUYER, UserRestMapper.toDomain(buyer).getRole());

        RegisterUserRequestDTO admin = new RegisterUserRequestDTO();
        admin.setRole("SUPERVISOR");
        assertEquals(SystemRole.SUPERVISOR, UserRestMapper.toDomain(admin).getRole());
        admin.setRole("GUEST");
        assertThrows(IllegalArgumentException.class, () -> UserRestMapper.toDomain(admin));
    }

    @Test
    @DisplayName("login response uses the Bearer scheme")
    void loginResponse() {
        assertEquals("Bearer", UserRestMapper.toLoginResponse("tok", user(), 3600L).getTokenType());
        assertEquals(Long.valueOf(3600L), UserRestMapper.toLoginResponse("tok", user(), 3600L).getExpiresInSeconds());
    }

    @Test
    @DisplayName("money round-trips through the response DTO")
    void money() {
        Money money = MoneyRestMapper.toDomain(new BigDecimal("10.5"), "USD");
        MoneyResponseDTO dto = MoneyRestMapper.toResponseDTO(money);
        assertEquals(new BigDecimal("10.50"), dto.getAmount());
        assertEquals("USD", dto.getCurrency());
        assertNull(MoneyRestMapper.toResponseDTO(null));
        assertThrows(IllegalArgumentException.class, () -> MoneyRestMapper.toDomain(BigDecimal.ONE, "BTC"));
    }

    @Test
    @DisplayName("entity stubs only carry the identifier and map null to null")
    void stubs() {
        assertEquals(Long.valueOf(5L), EntityStubs.product(5L).getId());
        assertEquals(Long.valueOf(6L), EntityStubs.order(6L).getId());
        assertNull(EntityStubs.warehouse(null));
        EntityReference ref = EntityStubs.entityReference("ORDER", "9");
        assertEquals(EntityType.ORDER, ref.getType());
        assertEquals("9", ref.getIdentifier());
        assertThrows(IllegalArgumentException.class, () -> EntityStubs.entityReference("NOPE", "1"));
    }

    @Test
    @DisplayName("product and order aggregates map to flat DTOs")
    void aggregates() {
        Seller seller = new Seller();
        seller.setId(3L);
        Product product = new Product(seller, "Lamp", "Desk lamp", Money.of(new BigDecimal("20"), Currency.COP),
                ProductType.PHYSICAL);
        product.setId(11L);
        ProductResponseDTO productDto = MarketRestMapper.product(product);
        assertEquals(Long.valueOf(3L), productDto.getSellerId());
        assertEquals("PHYSICAL", productDto.getType());
        assertEquals(new BigDecimal("20.00"), productDto.getPrice().getAmount());
        assertNotNull(productDto.getVariants());

        Order order = new Order();
        order.setId(21L);
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);
        item.setUnitPrice(product.getPrice());
        item.setSubtotal(product.getPrice().multiply(2));
        List<OrderItem> items = new ArrayList<>();
        items.add(item);
        order.setItems(items);
        OrderResponseDTO orderDto = MarketRestMapper.order(order);
        assertEquals(Long.valueOf(21L), orderDto.getId());
        assertEquals("PENDING_PAYMENT", orderDto.getStatus());
        assertEquals(1, orderDto.getItems().size());
        assertEquals(Long.valueOf(11L), orderDto.getItems().get(0).getProductId());
        assertEquals(new BigDecimal("40.00"), orderDto.getItems().get(0).getSubtotal().getAmount());
        assertNull(MarketRestMapper.order(null));
        assertEquals(0, MarketRestMapper.list(null, MarketRestMapper::order).size());
    }

    @Test
    @DisplayName("operation and audit entries expose actor and affected entity")
    void traceability() {
        Operation operation = new Operation();
        operation.setOperationId(1L);
        operation.setOperationType(OperationType.ORDER_CREATION);
        operation.setExecutionDate(LocalDateTime.of(2026, 1, 2, 3, 4));
        operation.setPerformedBy(user());
        operation.setAffectedEntity(EntityReference.of(EntityType.ORDER, 21L));
        OperationResponseDTO dto = MarketRestMapper.operation(operation);
        assertEquals("ORDER_CREATION", dto.getOperationType());
        assertEquals("ana", dto.getPerformedByUsername());
        assertEquals("ORDER", dto.getEntityType());
        assertEquals("21", dto.getEntityId());
    }
}
