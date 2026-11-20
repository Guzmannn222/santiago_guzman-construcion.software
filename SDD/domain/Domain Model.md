# Domain Model

## Introducción

Modelo de dominio de **NexusMarket** (marketplace). Los modelos viven en `application.domain.models`, son POJOs con Lombok, sin dependencias de Spring ni de persistencia, y protegen sus invariantes mediante validaciones en constructores/setters y matrices de transición de estado (`UserStatus`, `ProductStatus`, `OrderStatus`, `ShipmentStatus`, `ReturnStatus`, `RefundStatus`).

Los importes se modelan con el Value Object `Money` (BigDecimal escala 2 + `Currency`), nunca con `double`.

---

## AuditLog

Immutable, append-only audit record of an {@link Operation} (stored in MongoDB).

**Atributos**

| Atributo | Tipo |
|---|---|
| `auditId` | `String` |
| `operationType` | `OperationType` |
| `operationDate` | `LocalDateTime` |
| `performedBy` | `User` |
| `userRole` | `SystemRole` |
| `affectedEntity` | `EntityReference` |
| `details` | `Map<String, Object>` |

---

## AuthenticationResult

Outcome of a successful authentication: the authenticated user and the issued token.

**Atributos**

| Atributo | Tipo |
|---|---|
| `authenticatedUser` | `User` |
| `token` | `String` |

---

## Buyer

Buyer profile and delivery addresses. Business rule: a buyer never manages another buyer's information nor any inventory data.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `user` | `User` |
| `primaryAddress` | `String` |
| `additionalAddresses` | `List<String>` |
| `tradingStatus` | `TradingStatus` |
| `cart` | `Cart` |

**Comportamiento y reglas**

- `void addAddress(String address)`
- `void ensureCanPlaceOrders()`
- `boolean isOwnedBy(User candidate)`

---

## Cart

Provisional product selection made by a {@link Buyer} (cardinality 1-1 with its buyer).

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `buyer` | `Buyer` |
| `items` | `List<CartItem>` |
| `total` | `Money` |

**Comportamiento y reglas**

- `void addProduct(Product product, int quantity)`
- `void removeProduct(Product product)`
- `void updateQuantity(Product product, int quantity)`
- `void clear()`
- `Money calculateTotal()`
- `void confirm()`
- `boolean isOwnedBy(User candidate)`

---

## CartItem

A single product line inside a {@link Cart}. The unit price is a snapshot taken when added.

**Atributos**

| Atributo | Tipo |
|---|---|
| `product` | `Product` |
| `quantity` | `int` |
| `unitPrice` | `Money` |

---

## Inventory

Controls stock for a product in a warehouse (distributed inventory). Critical validations: stock can never go negative, and DAMAGED stock can never be reserved.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `product` | `Product` |
| `warehouse` | `Warehouse` |
| `stock` | `int` |
| `status` | `InventoryStatus` |
| `movements` | `List<InventoryMovement>` |
| `lowStockThreshold` | `int` |

**Comportamiento y reglas**

- `void inbound(int quantity)`
- `void reserve(int quantity)`
- `void withdraw(int quantity)`
- `void adjust(int newQuantity)`
- `void returnStock(int quantity)`
- `void markDamaged()`
- `void clearDamaged()`
- `boolean canFulfill(int quantity)`
- `void recordMovement(InventoryMovement movement)`
- `void refreshStatus()`

---

## InventoryMovement

Records a change of stock over an {@link Inventory} record.

**Atributos**

| Atributo | Tipo |
|---|---|
| `type` | `InventoryMovementType` |
| `quantity` | `int` |
| `date` | `LocalDateTime` |
| `reason` | `String` |

---

## Invoice

Commercial billing information tied to a paid {@link Order}.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `order` | `Order` |
| `subtotal` | `Money` |
| `tax` | `Money` |
| `total` | `Money` |
| `taxRate` | `BigDecimal` |
| `cancelled` | `boolean` |
| `issueDate` | `LocalDateTime` |

**Comportamiento y reglas**

- `void generate(BigDecimal taxRate)`
- `Money calculateTotal()`
- `void cancel()`

---

## Operation

Significant business action performed by a user over a domain entity (relational log).

**Atributos**

| Atributo | Tipo |
|---|---|
| `operationId` | `Long` |
| `operationType` | `OperationType` |
| `executionDate` | `LocalDateTime` |
| `performedBy` | `User` |
| `affectedEntity` | `EntityReference` |

---

## Order

Formal commercial commitment generated from a confirmed cart. Lifecycle: PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED -> FINISHED, or CANCELLED from PENDING_PAYMENT / PAID. FINISHED and CANCELLED are terminal: a closed order can never be modified again.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `buyer` | `Buyer` |
| `items` | `List<OrderItem>` |
| `total` | `Money` |
| `status` | `OrderStatus` |
| `date` | `LocalDateTime` |
| `warehouse` | `Warehouse` |

**Comportamiento y reglas**

- `void addItem(OrderItem item)`
- `Money recalculateTotal()`
- `void confirmPayment()`
- `void ship()`
- `void deliver()`
- `void deliverDigital()`
- `void finish()`
- `void cancel()`
- `void changeStatus(OrderStatus target)`
- `boolean canModify()`
- `boolean hasPhysicalItems()`
- `boolean isDelivered()`
- `boolean isPaidOrLater()`
- `boolean isOwnedBy(User candidate)`
- `boolean containsProductOf(Seller seller)`

---

## OrderItem

A single product line inside an {@link Order}.

**Atributos**

| Atributo | Tipo |
|---|---|
| `product` | `Product` |
| `quantity` | `int` |
| `unitPrice` | `Money` |
| `subtotal` | `Money` |

**Comportamiento y reglas**

- `Money calculateSubtotal()`

---

## Person

Identity and contact data shared by every identifiable person of the platform.

**Atributos**

| Atributo | Tipo |
|---|---|
| `identityDocument` | `String` |
| `name` | `String` |
| `email` | `String` |
| `role` | `SystemRole` |

---

## Product

Physical or digital good offered in the catalog. Status transitions: SUSPENDED <-> PUBLISHED, and either one -> DISCONTINUED (terminal).

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `seller` | `Seller` |
| `name` | `String` |
| `description` | `String` |
| `price` | `Money` |
| `type` | `ProductType` |
| `status` | `ProductStatus` |
| `variants` | `List<Variant>` |

**Comportamiento y reglas**

- `void addVariant(Variant variant)`
- `void publish()`
- `void suspend()`
- `void discontinue()`
- `void changeStatus(ProductStatus target)`
- `void updatePrice(Money newPrice)`
- `boolean isPublished()`
- `boolean isPhysical()`
- `void ensurePurchasable()`
- `boolean belongsTo(Seller candidate)`

---

## Refund

Money reimbursement originated by an approved {@link Return}. The amount can never exceed the value of the returned products. Transitions: PENDING -> PROCESSING -> COMPLETED, and PENDING | PROCESSING -> REJECTED.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `productReturn` | `Return` |
| `amount` | `Money` |
| `status` | `RefundStatus` |

**Comportamiento y reglas**

- `void process()`
- `void complete()`
- `void reject()`

---

## Report

Consolidated administrative query, generated on demand (not persisted).

**Atributos**

| Atributo | Tipo |
|---|---|
| `type` | `ReportType` |
| `filters` | `Map<String, Object>` |
| `data` | `Map<String, Object>` |
| `generationDate` | `LocalDateTime` |

**Comportamiento y reglas**

- `void filter(Map<String, Object> filters)`
- `void generate(Map<String, Object> data)`

---

## Return

Post-sale return request associated with a delivered {@link Order}. Transitions: REQUESTED -> APPROVED | REJECTED, APPROVED -> COMPLETED.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `order` | `Order` |
| `reason` | `String` |
| `status` | `ReturnStatus` |
| `items` | `List<ReturnItem>` |
| `requestDate` | `LocalDateTime` |

`ReturnItem` (`product`, `quantity`) indica qué productos y cuántas unidades se devuelven. Si no se envían líneas, se devuelve la orden completa. Cada producto debe pertenecer a la orden, no repetirse y no superar la cantidad comprada. Al completar la devolución solo se reponen al inventario las unidades devueltas, y el reembolso no puede superar su valor.

**Comportamiento y reglas**

- `void approve()`
- `void reject()`
- `void complete()`
- `boolean isRefundable()`

---

## Seller

Product provider. Business rule: sellers cannot self-register; they are onboarded by an Administrator together with their first warehouse (CU-03).

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `user` | `User` |
| `businessInfo` | `String` |
| `active` | `boolean` |
| `warehouses` | `List<Warehouse>` |

**Comportamiento y reglas**

- `void addWarehouse(Warehouse warehouse)`
- `void deactivate()`
- `void reactivate()`
- `void ensureActive()`
- `boolean isOwnedBy(User candidate)`

---

## Shipment

Physical logistics of an {@link Order}. Transitions: PENDING -> PREPARING -> DISPATCHED -> IN_TRANSIT -> DELIVERED (IN_TRANSIT is optional).

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `order` | `Order` |
| `address` | `String` |
| `status` | `ShipmentStatus` |
| `dispatchDate` | `LocalDateTime` |
| `deliveryDate` | `LocalDateTime` |

**Comportamiento y reglas**

- `void prepare()`
- `void dispatch()`
- `void updateStatus(ShipmentStatus newStatus)`
- `void confirmDelivery()`

---

## User

System identity used for authentication and authorization. Each user has exactly one role (RG-02). identityDocument, email and username must be unique (enforced by the registration services).

**Atributos**

| Atributo | Tipo |
|---|---|
| `EMAIL` | `Pattern` |
| `id` | `Long` |
| `username` | `String` |
| `password` | `String` |
| `status` | `UserStatus` |
| `authTokenVersion` | `Integer` |

**Comportamiento y reglas**

- `void changeStatus(UserStatus target)`
- `void activate()`
- `void block()`
- `void changeRole(SystemRole newRole)`
- `void bumpAuthTokenVersion()`
- `boolean validateAccess()`
- `boolean isSameUserAs(User other)`
- `void ensureRegistrationDataComplete()`
- `void validatePasswordFormat(String rawPassword)`

---

## Variant

A product variation, such as color, size or model.

**Atributos**

| Atributo | Tipo |
|---|---|
| `name` | `String` |
| `value` | `String` |

---

## Warehouse

Physical storage space, owned either by the marketplace or by a seller.

**Atributos**

| Atributo | Tipo |
|---|---|
| `id` | `Long` |
| `location` | `String` |
| `type` | `WarehouseType` |
| `active` | `boolean` |
| `owner` | `Seller` |

**Comportamiento y reglas**

- `void deactivate()`
- `void ensureActive()`

