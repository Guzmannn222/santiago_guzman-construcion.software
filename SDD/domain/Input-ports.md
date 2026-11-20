# Input Ports

## Introducción

Puertos de entrada (`application.domain.ports.in`): contratos que el dominio expone a los adaptadores REST. Existe un puerto por caso de uso (`<Accion>UseCase`) y una **fachada por rol** que agrupa los casos de uso de cada actor. Todos los métodos reciben el `User actor` autenticado como primer parámetro: el dominio decide si está activo y si su rol está autorizado.

## Fachadas por rol

### AdministratorPort

Every use case available to the ADMINISTRATOR role (CU-01, CU-03, CU-04, CU-10, CU-14, CU-15 and audit).

- `void changeMyPassword(User admin, String currentPassword, String newPassword)`
- `User registerUser(User admin, User newUser)`
- `void blockUser(User admin, User target)`
- `void activateUser(User admin, User target)`
- `void changeUserRole(User admin, User target, SystemRole newRole)`
- `User consultUser(User admin, User target)`
- `List<User> consultUsers(User admin)`
- `Seller registerSeller(User admin, User sellerUser, String businessInfo, String firstWarehouseLocation)`
- `void deactivateSeller(User admin, Seller seller)`
- `void reactivateSeller(User admin, Seller seller)`
- `List<Seller> consultSellers(User admin)`
- `Warehouse registerWarehouse(User admin, String location, WarehouseType type, Seller owner)`
- `void deactivateWarehouse(User admin, Warehouse warehouse)`
- `List<Warehouse> consultWarehouses(User admin)`
- `List<Order> consultOrders(User admin)`
- `Invoice generateInvoice(User admin, Order order, BigDecimal taxRate)`
- `void cancelInvoice(User admin, Invoice invoice)`
- `List<Invoice> consultInvoices(User admin)`
- `List<Return> consultReturns(User admin)`
- `Refund issueRefund(User admin, Return productReturn, Money amount)`
- `void processRefund(User admin, Refund refund)`
- `void completeRefund(User admin, Refund refund)`
- `void rejectRefund(User admin, Refund refund)`
- `List<Refund> consultRefunds(User admin)`
- `Report generateSalesReport(User admin, Map<String, Object> filters)`
- `Report generateInventoryReport(User admin, Map<String, Object> filters)`
- `Report generateReturnsReport(User admin, Map<String, Object> filters)`
- `List<AuditLog> consultAuditLogsByUser(User admin, User user)`
- `List<AuditLog> consultAuditLogsByEntity(User admin, EntityReference entity)`
- `List<Operation> consultOperationsByUser(User admin, User user)`

### BuyerPort

Every use case available to the BUYER role (CU-02, CU-08, CU-09, CU-13 and read access to the published catalog).

- `Buyer registerMyProfile(User user, String primaryAddress)`
- `Buyer consultMyProfile(User user)`
- `void addMyAddress(User user, Buyer buyer, String address)`
- `void changeMyPassword(User user, String currentPassword, String newPassword)`
- `List<Product> browsePublishedCatalog(User user)`
- `Product consultProduct(User user, Product product)`
- `Cart consultMyCart(User user)`
- `void addToCart(User user, Cart cart, Product product, int quantity)`
- `void removeFromCart(User user, Cart cart, Product product)`
- `void updateCartItemQuantity(User user, Cart cart, Product product, int quantity)`
- `Order createOrder(User user, Cart cart, Warehouse warehouse)`
- `void confirmPayment(User user, Order order)`
- `void cancelOrder(User user, Order order)`
- `Order consultOrder(User user, Order order)`
- `List<Order> consultMyOrders(User user)`
- `List<Invoice> consultMyInvoices(User user)`
- `List<Shipment> consultMyShipments(User user)`
- `Return requestReturn(User user, Order order, String reason)`
- `List<Return> consultMyReturns(User user)`
- `List<Refund> consultMyRefunds(User user)`

### LogisticsOperatorPort

Every use case available to the LOGISTICS_OPERATOR role (CU-04, CU-07, CU-11, CU-12, CU-13 goods reception).

- `void changeMyPassword(User user, String currentPassword, String newPassword)`
- `Warehouse registerWarehouse(User user, String location, WarehouseType type, Seller owner)`
- `void deactivateWarehouse(User user, Warehouse warehouse)`
- `List<Warehouse> consultWarehouses(User user)`
- `Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock)`
- `void receiveStock(User user, Inventory inventory, int quantity, String reason)`
- `void adjustStock(User user, Inventory inventory, int newQuantity, String reason)`
- `void returnStock(User user, Inventory inventory, int quantity, String reason)`
- `void markInventoryDamaged(User user, Inventory inventory)`
- `List<Inventory> consultInventory(User user)`
- `List<Order> consultOrders(User user)`
- `Shipment createShipment(User user, Order order, String address)`
- `void prepareShipment(User user, Shipment shipment)`
- `void dispatchShipment(User user, Shipment shipment)`
- `void updateShipmentStatus(User user, Shipment shipment, ShipmentStatus status)`
- `void confirmDelivery(User user, Shipment shipment)`
- `List<Shipment> consultShipments(User user)`
- `void completeReturn(User user, Return productReturn)`
- `List<Return> consultReturns(User user)`

### PublicAccessPort

Public (unauthenticated) operations: login, logout and buyer self-registration.

- `AuthenticationResult login(User credentials)`
- `void logout(User authenticatedUser)`
- `Buyer registerBuyer(User newUser, String primaryAddress)`

### SellerPort

Every use case available to the SELLER role (CU-05, CU-06, CU-07, CU-13 decisions, CU-14).

- `Seller consultMyProfile(User user)`
- `void changeMyPassword(User user, String currentPassword, String newPassword)`
- `Product createProduct(User user, Seller seller, String name, String description, Money price, ProductType type)`
- `void addProductVariant(User user, Product product, String name, String value)`
- `void updateProductPrice(User user, Product product, Money newPrice)`
- `void publishProduct(User user, Product product)`
- `void suspendProduct(User user, Product product)`
- `void discontinueProduct(User user, Product product)`
- `Product consultProduct(User user, Product product)`
- `List<Product> consultMyProducts(User user)`
- `Inventory openInventory(User user, Product product, Warehouse warehouse, int initialStock)`
- `void receiveStock(User user, Inventory inventory, int quantity, String reason)`
- `void adjustStock(User user, Inventory inventory, int newQuantity, String reason)`
- `void markInventoryDamaged(User user, Inventory inventory)`
- `List<Inventory> consultMyInventory(User user)`
- `List<Warehouse> consultWarehouses(User user)`
- `List<Order> consultMyOrders(User user)`
- `void approveReturn(User user, Return productReturn)`
- `void rejectReturn(User user, Return productReturn)`
- `List<Return> consultMyReturns(User user)`
- `Refund issueRefund(User user, Return productReturn, Money amount)`
- `void processRefund(User user, Refund refund)`
- `void completeRefund(User user, Refund refund)`
- `void rejectRefund(User user, Refund refund)`
- `List<Refund> consultMyRefunds(User user)`

### SupervisorPort

Every use case available to the SUPERVISOR role (CU-15): read-only monitoring, it never mutates orders, inventory or refunds.

- `void changeMyPassword(User supervisor, String currentPassword, String newPassword)`
- `Report generateSalesReport(User supervisor, Map<String, Object> filters)`
- `Report generateInventoryReport(User supervisor, Map<String, Object> filters)`
- `Report generateReturnsReport(User supervisor, Map<String, Object> filters)`
- `List<Order> consultOrders(User supervisor)`
- `List<Inventory> consultInventory(User supervisor)`
- `List<Return> consultReturns(User supervisor)`
- `List<Refund> consultRefunds(User supervisor)`
- `List<AuditLog> consultAuditLogsByUser(User supervisor, User user)`
- `List<AuditLog> consultAuditLogsByEntity(User supervisor, EntityReference entity)`
- `List<AuditLog> consultAuditLogsByOperationType(User supervisor, OperationType operationType)`
- `List<Operation> consultOperationsByUser(User supervisor, User user)`
- `List<Operation> consultOperationsByEntity(User supervisor, EntityReference entity)`

## Casos de uso

| Puerto | Firma | Descripción |
|---|---|---|
| `ActivateUserUseCase` | `void activate(User actor, User target)` | Input Port: CU-01 activate a user (role ADMINISTRATOR). |
| `AddBuyerAddressUseCase` | `void addAddress(User actor, Buyer buyer, String address)` | Input Port: CU-02 add a delivery address to the actor own buyer profile. |
| `AddCartItemUseCase` | `void addProduct(User actor, Cart cart, Product product, int quantity)` | Input Port: CU-08 add a published product to the own cart (role BUYER). |
| `AddProductVariantUseCase` | `void addVariant(User actor, Product product, String name, String value)` | Input Port: CU-05 add a variant to an own product (role SELLER). |
| `AdjustStockUseCase` | `void adjustStock(User actor, Inventory inventory, int newQuantity, String reason)` | Input Port: CU-07 set the absolute stock after a physical count (roles SELLER, LOGISTICS_OPERATOR). |
| `ApproveReturnUseCase` | `void approve(User actor, Return productReturn)` | Input Port: CU-13 approve a return of an order with own products (role SELLER). |
| `BlockUserUseCase` | `void block(User actor, User target)` | Input Port: CU-01 block a user (role ADMINISTRATOR). |
| `CancelInvoiceUseCase` | `void cancelInvoice(User actor, Invoice invoice)` | Input Port: CU-10 cancel an invoice (role ADMINISTRATOR). |
| `CancelOrderUseCase` | `void cancelOrder(User actor, Order order)` | Input Port: CU-09 cancel an own order before it ships, releasing the reserved stock (role BUYER). |
| `ChangeUserPasswordUseCase` | `void changePassword(User actor, String currentPassword, String newPassword)` | Input Port: change the password of the acting user (invalidates previous tokens). |
| `ChangeUserRoleUseCase` | `void changeRole(User actor, User target, SystemRole newRole)` | Input Port: CU-01 change the role of a user (role ADMINISTRATOR). |
| `CheckStockAvailabilityUseCase` | `boolean checkAvailability(User actor, Inventory inventory, int quantity)` | Input Port: CU-07 check whether a quantity can be reserved (role BUYER). |
| `CompleteRefundUseCase` | `void complete(User actor, Refund refund)` | Input Port: CU-14 complete a refund (roles ADMINISTRATOR, SELLER). |
| `CompleteReturnUseCase` | `void complete(User actor, Return productReturn)` | Input Port: CU-13 complete a return once goods are received back, restocking physical items (role LOGISTICS_OPERATOR). |
| `ConfirmDeliveryUseCase` | `void confirmDelivery(User actor, Shipment shipment)` | Input Port: CU-12 confirm the delivery of a shipment, finishing its order (role LOGISTICS_OPERATOR). |
| `ConfirmOrderPaymentUseCase` | `void confirmPayment(User actor, Order order)` | Input Port: CU-09 confirm the payment of an own order (role BUYER). |
| `ConsultAuditLogsUseCase` | `List<AuditLog> consultByUser(User actor, User user)` | Input Port: consult the audit trail (roles SUPERVISOR, ADMINISTRATOR). |
| `ConsultBuyerUseCase` | `Buyer consultBuyer(User actor, Buyer buyer)` | Input Port: CU-02 consult buyers. A buyer only sees its own profile. |
| `ConsultCartUseCase` | `Cart consultCart(User actor, Cart cart)` | Input Port: CU-08 consult a cart. A buyer only sees its own cart. |
| `ConsultInventoryUseCase` | `Inventory consultInventory(User actor, Inventory inventory)` | Input Port: CU-07 consult inventory. SELLER only sees inventory of its own products or warehouses. |
| `ConsultInvoiceUseCase` | `Invoice consultInvoice(User actor, Invoice invoice)` | Input Port: CU-10 consult invoices. BUYER only sees invoices of its own orders. |
| `ConsultOperationsUseCase` | `List<Operation> consultByUser(User actor, User user)` | Input Port: consult the operations log (roles SUPERVISOR, ADMINISTRATOR). |
| `ConsultOrderUseCase` | `Order consultOrder(User actor, Order order)` | Input Port: CU-09 consult orders. BUYER sees its own, SELLER the ones with its products, the rest of the roles see all. |
| `ConsultProductUseCase` | `Product consultProduct(User actor, Product product)` | Input Port: CU-05/CU-06 consult products. Published products are visible to any authenticated user. |
| `ConsultRefundUseCase` | `Refund consultRefund(User actor, Refund refund)` | Input Port: CU-14 consult refunds. BUYER sees its own, SELLER the ones of its products. |
| `ConsultReturnUseCase` | `Return consultReturn(User actor, Return productReturn)` | Input Port: CU-13 consult returns. BUYER sees its own, SELLER the ones of its products. |
| `ConsultSellerUseCase` | `Seller consultSeller(User actor, Seller seller)` | Input Port: CU-03 consult sellers. A seller only sees its own profile. |
| `ConsultShipmentUseCase` | `Shipment consultShipment(User actor, Shipment shipment)` | Input Port: CU-11/CU-12 consult shipments. BUYER only sees shipments of its own orders. |
| `ConsultUserUseCase` | `User consultUser(User actor, User target)` | Input Port: CU-01 consult users. A user can consult itself; ADMINISTRATOR can consult any user. |
| `ConsultWarehouseUseCase` | `Warehouse consultWarehouse(User actor, Warehouse warehouse)` | Input Port: CU-04 consult warehouses. |
| `CreateOrderUseCase` | `Order createOrder(User actor, Cart cart, Warehouse warehouse)` | Input Port: CU-09 create an order from the own cart, reserving stock. {@code warehouse} may be null: the service picks one able to fulfill the order (role BUYER). |
| `CreateProductUseCase` | `Product createProduct(User actor, Seller seller, String name, String description, Money price, ProductType type)` | Input Port: CU-05 create a product (role SELLER). {@code seller} may be null or a stub: the service resolves the actor own seller profile and rejects any other seller. |
| `CreateShipmentUseCase` | `Shipment createShipment(User actor, Order order, String address)` | Input Port: CU-11 create the shipment of a paid order with physical items (role LOGISTICS_OPERATOR). A null address uses the buyer primary address. |
| `DeactivateSellerUseCase` | `void deactivate(User actor, Seller seller)` | Input Port: CU-03 deactivate a seller (role ADMINISTRATOR). |
| `DeactivateWarehouseUseCase` | `void deactivateWarehouse(User actor, Warehouse warehouse)` | Input Port: CU-04 deactivate a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR). |
| `DiscontinueProductUseCase` | `void discontinue(User actor, Product product)` | Input Port: CU-06 discontinue an own product (role SELLER). |
| `DispatchShipmentUseCase` | `void dispatch(User actor, Shipment shipment)` | Input Port: CU-11 dispatch a shipment (role LOGISTICS_OPERATOR). |
| `GenerateInventoryReportUseCase` | `Report generateInventoryReport(User actor, Map<String, Object> filters)` | Input Port: CU-15 inventory report (roles SUPERVISOR, ADMINISTRATOR; read-only). |
| `GenerateInvoiceUseCase` | `Invoice generateInvoice(User actor, Order order, BigDecimal taxRate)` | Input Port: CU-10 generate the invoice of a paid order (role ADMINISTRATOR). A null tax rate uses the configured default. |
| `GenerateReturnsReportUseCase` | `Report generateReturnsReport(User actor, Map<String, Object> filters)` | Input Port: CU-15 returns and refunds report (roles SUPERVISOR, ADMINISTRATOR; read-only). |
| `GenerateSalesReportUseCase` | `Report generateSalesReport(User actor, Map<String, Object> filters)` | Input Port: CU-15 sales report (roles SUPERVISOR, ADMINISTRATOR; read-only). |
| `IssueRefundUseCase` | `Refund issueRefund(User actor, Return productReturn, Money amount)` | Input Port: CU-14 issue the refund of a refundable return (roles ADMINISTRATOR, SELLER). |
| `LoginUseCase` | `AuthenticationResult login(User credentials)` | Input Port: authenticate a user and issue a token (unknown user and wrong password are indistinguishable). |
| `LogoutUseCase` | `void logout(User actor)` | Input Port: invalidate every token issued to the user. |
| `MarkInventoryDamagedUseCase` | `void markDamaged(User actor, Inventory inventory)` | Input Port: CU-07 mark an inventory record as damaged (roles SELLER, LOGISTICS_OPERATOR). |
| `OpenInventoryUseCase` | `Inventory openInventory(User actor, Product product, Warehouse warehouse, int initialStock)` | Input Port: CU-07 open an inventory record for a physical product in a warehouse (roles SELLER, LOGISTICS_OPERATOR). |
| `PrepareShipmentUseCase` | `void prepare(User actor, Shipment shipment)` | Input Port: CU-11 prepare a shipment (role LOGISTICS_OPERATOR). |
| `ProcessRefundUseCase` | `void process(User actor, Refund refund)` | Input Port: CU-14 start processing a refund (roles ADMINISTRATOR, SELLER). |
| `PublishProductUseCase` | `void publish(User actor, Product product)` | Input Port: CU-06 publish an own product (role SELLER). |
| `ReactivateSellerUseCase` | `void reactivate(User actor, Seller seller)` | Input Port: CU-03 reactivate a seller (role ADMINISTRATOR). |
| `ReceiveStockUseCase` | `void receiveStock(User actor, Inventory inventory, int quantity, String reason)` | Input Port: CU-07 receive stock (roles SELLER, LOGISTICS_OPERATOR). |
| `RegisterBuyerAccountUseCase` | `Buyer registerBuyerAccount(User newUser, String primaryAddress)` | Input Port: CU-02 public self-registration: creates a BUYER user, its buyer profile and its cart. |
| `RegisterBuyerUseCase` | `Buyer registerBuyer(User actor, String primaryAddress)` | Input Port: CU-02 create the buyer profile (and cart) of the authenticated BUYER user. |
| `RegisterSellerUseCase` | `Seller registerSeller(User actor, User sellerUser, String businessInfo, String firstWarehouseLocation)` | Input Port: CU-03 onboard a seller together with its first warehouse (role ADMINISTRATOR). |
| `RegisterUserUseCase` | `User register(User actor, User newUser)` | Input Port: CU-01 register a user (role ADMINISTRATOR). The new user carries username, plain password, name, email, identity document and role. |
| `RegisterWarehouseUseCase` | `Warehouse registerWarehouse(User actor, String location, WarehouseType type, Seller owner)` | Input Port: CU-04 register a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR). |
| `RejectRefundUseCase` | `void reject(User actor, Refund refund)` | Input Port: CU-14 reject a refund (roles ADMINISTRATOR, SELLER). |
| `RejectReturnUseCase` | `void reject(User actor, Return productReturn)` | Input Port: CU-13 reject a return of an order with own products (role SELLER). |
| `RemoveCartItemUseCase` | `void removeProduct(User actor, Cart cart, Product product)` | Input Port: CU-08 remove a product from the own cart (role BUYER). |
| `RequestReturnUseCase` | `Return requestReturn(User actor, Order order, String reason)` | Input Port: CU-13 request the return of an own delivered order (role BUYER). |
| `ReserveStockUseCase` | `void reserveStock(User actor, Inventory inventory, int quantity, String reason)` | Input Port: CU-07 reserve stock for a pending order (role BUYER, from checkout). |
| `ReturnStockUseCase` | `void returnStock(User actor, Inventory inventory, int quantity, String reason)` | Input Port: CU-07 return units to the warehouse (roles LOGISTICS_OPERATOR, SELLER). |
| `SuspendProductUseCase` | `void suspend(User actor, Product product)` | Input Port: CU-06 suspend an own product (role SELLER). |
| `UpdateCartItemQuantityUseCase` | `void updateQuantity(User actor, Cart cart, Product product, int quantity)` | Input Port: CU-08 change the quantity of a cart item (role BUYER). |
| `UpdateProductPriceUseCase` | `void updatePrice(User actor, Product product, Money newPrice)` | Input Port: CU-05 update the price of an own product (role SELLER). |
| `UpdateShipmentStatusUseCase` | `void updateStatus(User actor, Shipment shipment, ShipmentStatus status)` | Input Port: CU-11 update the tracking status of a shipment (role LOGISTICS_OPERATOR). |
