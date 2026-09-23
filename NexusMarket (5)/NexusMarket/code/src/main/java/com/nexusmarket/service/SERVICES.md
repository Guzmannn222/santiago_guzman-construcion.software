# NexusMarket -- Service Catalog (Use Cases)

This table lists every service (public method) implemented in this package. Each one maps 1:1 to
a use case and is restricted, at runtime, to the user role(s) noted below via
`com.nexusmarket.util.AccessControl.requireRole(...)` (or `requireSelfOrRole(...)`), which throws
`AccessDeniedException` when the acting `User` does not hold an allowed role or is not active.

**53 services / use cases**, minimum 1 authorized role each, across 11 service classes.

| # | Use case | Service class | Method | Allowed role(s) |
|---|----------|----------------|--------|------------------|
| UC-01 | Register user | `UserService` | `register` | ADMINISTRATOR |
| UC-02 | Authenticate user | `UserService` | `authenticate` | Any registered user (self login) |
| UC-03 | Block user | `UserService` | `block` | ADMINISTRATOR |
| UC-04 | Activate user | `UserService` | `activate` | ADMINISTRATOR |
| UC-05 | Change user role | `UserService` | `changeRole` | ADMINISTRATOR |
| UC-06 | Update own profile | `UserService` | `updateProfile` | Owner of the account, or ADMINISTRATOR |
| UC-07 | Register buyer profile | `BuyerService` | `registerBuyer` | BUYER |
| UC-08 | Add secondary delivery address | `BuyerService` | `addAddress` | BUYER (owner) |
| UC-09 | Onboard seller | `SellerService` | `registerSeller` | ADMINISTRATOR |
| UC-10 | Deactivate seller | `SellerService` | `deactivate` | ADMINISTRATOR |
| UC-11 | Reactivate seller | `SellerService` | `reactivate` | ADMINISTRATOR |
| UC-12 | Register warehouse | `WarehouseService` | `registerWarehouse` | ADMINISTRATOR |
| UC-13 | Deactivate warehouse | `WarehouseService` | `deactivateWarehouse` | ADMINISTRATOR |
| UC-14 | Create product | `ProductService` | `createProduct` | SELLER |
| UC-15 | Add product variant | `ProductService` | `addVariant` | SELLER |
| UC-16 | Publish product | `ProductService` | `publish` | SELLER |
| UC-17 | Suspend product | `ProductService` | `suspend` | SELLER |
| UC-18 | Discontinue product | `ProductService` | `discontinue` | SELLER |
| UC-19 | Update product price | `ProductService` | `updatePrice` | SELLER |
| UC-20 | Browse published catalog | `ProductService` | `findPublished` | BUYER |
| UC-21 | Open inventory record | `InventoryService` | `openInventory` | LOGISTICS_OPERATOR |
| UC-22 | Receive / inbound stock | `InventoryService` | `receiveStock` | LOGISTICS_OPERATOR |
| UC-23 | Reserve stock for a pending order | `InventoryService` | `reserveStock` | BUYER (via checkout) |
| UC-24 | Confirm sale outbound movement | `InventoryService` | `confirmSaleOutbound` | LOGISTICS_OPERATOR |
| UC-25 | Adjust stock after a physical count | `InventoryService` | `adjustStock` | LOGISTICS_OPERATOR |
| UC-26 | Return stock after approved return | `InventoryService` | `returnStock` | LOGISTICS_OPERATOR |
| UC-27 | Check stock availability | `InventoryService` | `checkAvailability` | BUYER |
| UC-28 | Create cart | `CartService` | `createCart` | BUYER |
| UC-29 | Add product to cart | `CartService` | `addProduct` | BUYER |
| UC-30 | Remove product from cart | `CartService` | `removeProduct` | BUYER |
| UC-31 | Update cart item quantity | `CartService` | `updateQuantity` | BUYER |
| UC-32 | Create order from confirmed cart | `OrderService` | `createOrder` | BUYER |
| UC-33 | Confirm order payment | `OrderService` | `confirmPayment` | BUYER |
| UC-34 | Cancel order (before shipped) | `OrderService` | `cancelOrder` | BUYER |
| UC-35 | Generate invoice | `InvoiceService` | `generateInvoice` | ADMINISTRATOR |
| UC-36 | Cancel invoice | `InvoiceService` | `cancelInvoice` | ADMINISTRATOR |
| UC-37 | Create shipment | `ShipmentService` | `createShipment` | LOGISTICS_OPERATOR |
| UC-38 | Prepare shipment (packing) | `ShipmentService` | `prepare` | LOGISTICS_OPERATOR |
| UC-39 | Dispatch shipment | `ShipmentService` | `dispatch` | LOGISTICS_OPERATOR |
| UC-40 | Update shipment tracking status | `ShipmentService` | `updateStatus` | LOGISTICS_OPERATOR |
| UC-41 | Confirm delivery | `ShipmentService` | `confirmDelivery` | LOGISTICS_OPERATOR |
| UC-42 | Request return | `ReturnService` | `requestReturn` | BUYER |
| UC-43 | Approve return request | `ReturnService` | `approve` | SUPERVISOR |
| UC-44 | Reject return request | `ReturnService` | `reject` | SUPERVISOR |
| UC-45 | Complete return | `ReturnService` | `complete` | LOGISTICS_OPERATOR |
| UC-46 | Issue refund | `RefundService` | `issueRefund` | SUPERVISOR |
| UC-47 | Process refund payment | `RefundService` | `process` | SUPERVISOR |
| UC-48 | Complete refund | `RefundService` | `complete` | SUPERVISOR |
| UC-49 | Reject refund | `RefundService` | `reject` | SUPERVISOR |
| UC-50 | Generate sales report | `ReportService` | `generateSalesReport` | SUPERVISOR |
| UC-51 | Generate inventory report | `ReportService` | `generateInventoryReport` | SUPERVISOR |
| UC-52 | Generate returns/refunds report | `ReportService` | `generateReturnsReport` | SUPERVISOR |
| UC-53 | Generate seller performance report | `ReportService` | `generateSellerPerformanceReport` | ADMINISTRATOR |

## How authorization works

Every service method (except `authenticate`, which *is* the login action) receives the acting
`User` as its first parameter and starts with a call to `AccessControl`:

```java
public void publish(User actor, Product product) {
    AccessControl.requireRole(actor, Role.SELLER);
    product.publish();
    productRepository.save(product);
}
```

`AccessControl.requireRole` checks two things: that `actor` is `ACTIVE` (RG-01) and that
`actor.getRole()` is one of the roles allowed for that service (RG-02). If either check fails, it
throws `com.nexusmarket.util.AccessDeniedException`. `AccessControl.requireSelfOrRole` is used for
services where the resource owner may also act on their own data (e.g. `UserService.updateProfile`).

See `UserServiceTest.shouldNotAllowANonAdministratorToRegisterUsers` for a working example that
exercises this rule.
