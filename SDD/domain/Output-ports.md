# Output Ports

## Introducción

Puertos de salida (`application.domain.ports.out`): el dominio los define y los adaptadores de infraestructura los implementan (JPA/MySQL, MongoDB, BCrypt, JJWT, notificaciones, configuración de negocio). Los puertos nunca exponen tipos de Spring, JPA ni JJWT.

## AuditLogRepositoryPort

Output port: immutable audit trail (MongoDB, append-only).

- `AuditLog save(AuditLog auditLog)`
- `List<AuditLog> findByUserId(Long userId)`
- `List<AuditLog> findByEntity(EntityReference entity)`
- `List<AuditLog> findByOperationType(OperationType operationType)`

## BusinessConfigurationPort

Output port: business-level configuration (externalized in application.properties).

- `BigDecimal getDefaultTaxRate()`
- `int getLowStockThreshold()`

## BuyerRepositoryPort

Output port: persistence of Buyer aggregates.

- `Buyer save(Buyer buyer)`
- `void update(Buyer buyer)`
- `Optional<Buyer> findById(Long id)`
- `List<Buyer> findAll()`
- `Optional<Buyer> findByUserId(Long userId)`

## CartRepositoryPort

Output port: persistence of Cart aggregates.

- `Cart save(Cart cart)`
- `void update(Cart cart)`
- `Optional<Cart> findById(Long id)`
- `List<Cart> findAll()`
- `Optional<Cart> findByBuyerId(Long buyerId)`

## InventoryRepositoryPort

Output port: persistence of Inventory aggregates.

- `Inventory save(Inventory inventory)`
- `void update(Inventory inventory)`
- `Optional<Inventory> findById(Long id)`
- `List<Inventory> findAll()`
- `Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse)`
- `List<Inventory> findByProductId(Long productId)`
- `List<Inventory> findByWarehouseId(Long warehouseId)`

## InvoiceRepositoryPort

Output port: persistence of Invoice aggregates.

- `Invoice save(Invoice invoice)`
- `void update(Invoice invoice)`
- `Optional<Invoice> findById(Long id)`
- `List<Invoice> findAll()`
- `Optional<Invoice> findByOrderId(Long orderId)`

## JwtServicePort

Output port: token issuing and validation. The domain never depends on a JWT library.

- `String generateToken(User user)`
- `boolean validateToken(String token)`
- `Long extractUserId(String token)`
- `Integer extractTokenVersion(String token)`
- `User reconstructUser(String token)`

## NotificationPort

Output port: notify a user (e.g. order delivered, return approved).

- `void notifyUser(User user, String message, NotificationChannel channel)`

## OperationRepositoryPort

Output port: relational log of business operations (append-only).

- `Operation save(Operation operation)`
- `List<Operation> findByUserId(Long userId)`
- `List<Operation> findByEntity(EntityReference entity)`

## OrderRepositoryPort

Output port: persistence of Order aggregates.

- `Order save(Order order)`
- `void update(Order order)`
- `Optional<Order> findById(Long id)`
- `List<Order> findAll()`
- `List<Order> findByBuyer(Buyer buyer)`

## PasswordServicePort

Output port: password hashing (BCrypt in the adapter layer).

- `boolean matches(String rawPassword, String encodedPassword)`
- `String encrypt(String rawPassword)`

## ProductRepositoryPort

Output port: persistence of Product aggregates.

- `Product save(Product product)`
- `void update(Product product)`
- `Optional<Product> findById(Long id)`
- `List<Product> findAll()`
- `List<Product> findByStatus(ProductStatus status)`
- `List<Product> findBySellerId(Long sellerId)`

## RefundRepositoryPort

Output port: persistence of Refund aggregates.

- `Refund save(Refund refund)`
- `void update(Refund refund)`
- `Optional<Refund> findById(Long id)`
- `List<Refund> findAll()`
- `Optional<Refund> findByReturnId(Long returnId)`

## ReturnRepositoryPort

Output port: persistence of Return aggregates.

- `Return save(Return productReturn)`
- `void update(Return productReturn)`
- `Optional<Return> findById(Long id)`
- `List<Return> findAll()`
- `List<Return> findByOrderId(Long orderId)`

## SellerRepositoryPort

Output port: persistence of Seller aggregates.

- `Seller save(Seller seller)`
- `void update(Seller seller)`
- `Optional<Seller> findById(Long id)`
- `List<Seller> findAll()`
- `Optional<Seller> findByUserId(Long userId)`

## ShipmentRepositoryPort

Output port: persistence of Shipment aggregates.

- `Shipment save(Shipment shipment)`
- `void update(Shipment shipment)`
- `Optional<Shipment> findById(Long id)`
- `List<Shipment> findAll()`
- `Optional<Shipment> findByOrderId(Long orderId)`

## UserRepositoryPort

Output port: persistence of User aggregates.

- `User save(User user)`
- `void update(User user)`
- `Optional<User> findById(Long id)`
- `List<User> findAll()`
- `Optional<User> findByUsername(String username)`
- `Optional<User> findByEmail(String email)`
- `Optional<User> findByIdentityDocument(String identityDocument)`

## WarehouseRepositoryPort

Output port: persistence of Warehouse aggregates.

- `Warehouse save(Warehouse warehouse)`
- `void update(Warehouse warehouse)`
- `Optional<Warehouse> findById(Long id)`
- `List<Warehouse> findAll()`
- `List<Warehouse> findByOwnerId(Long sellerId)`

