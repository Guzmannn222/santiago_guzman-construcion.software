# Domain Services

## Introducción

Los servicios de dominio (`application.domain.services.<subdominio>`) implementan los puertos de entrada. Cada servicio sigue el mismo patrón: (1) valida que el actor esté `ACTIVE` (`ValidateUserAuthorizationStatusService`), (2) valida el rol permitido (`ValidateRoleAuthorizationService`), (3) valida los datos y recarga las entidades por id, (4) aplica reglas de negocio de los modelos, (5) persiste mediante puertos de salida y (6) registra operación + auditoría (`RegisterOperationAndAuditService`).

Subdominios documentados por archivo en [`services/`](services/):

- [User and Authentication Services](services/user-authentication-services.md) — Login, logout, registro de usuarios, cambio de contraseña/rol/estado y consulta de usuarios.
- [Buyer Services](services/buyer-services.md) — Alta y consulta del perfil de comprador y de sus direcciones.
- [Seller and Warehouse Services](services/seller-warehouse-services.md) — Gestión de vendedores y bodegas.
- [Product Services](services/product-services.md) — Ciclo de vida del producto: creación, variantes, precio, publicación, suspensión y descontinuación.
- [Inventory Services](services/inventory-services.md) — Apertura, recepción, reserva, ajuste, devolución y marcado de daño de inventario.
- [Cart and Order Services](services/cart-order-services.md) — Carrito de compras y pedidos: creación, pago y cancelación.
- [Invoice and Shipment Services](services/invoice-shipment-services.md) — Facturación y logística de envíos.
- [Return and Refund Services](services/return-refund-services.md) — Devoluciones y reembolsos.
- [Report Services](services/report-services.md) — Generación de reportes de ventas, inventario y devoluciones/reembolsos.
- [Operation and Audit Services](services/operation-audit-services.md) — Trazabilidad: operaciones (MySQL) y bitácora de auditoría (MongoDB).
- [Authorization Services](services/authorization-services.md) — validación de estado y rol del actor.

## Servicios implementados

| Paquete | Servicios |
|---|---|
| `authorization` | `ValidateRoleAuthorizationService`, `ValidateUserAuthorizationStatusService` |
| `buyer` | `AddBuyerAddressService`, `ConsultBuyerService`, `RegisterBuyerAccountService`, `RegisterBuyerService` |
| `cart` | `AddCartItemService`, `ConsultCartService`, `LoadOwnedCartService`, `RemoveCartItemService`, `UpdateCartItemQuantityService` |
| `inventory` | `AdjustStockService`, `CheckStockAvailabilityService`, `ConsultInventoryService`, `LoadManagedInventoryService`, `MarkInventoryDamagedService`, `OpenInventoryService`, `ReceiveStockService`, `ReserveStockService`, `ReturnStockService` |
| `invoice` | `CancelInvoiceService`, `ConsultInvoiceService`, `GenerateInvoiceService` |
| `operation` | `ConsultAuditLogsService`, `ConsultOperationsService`, `RegisterAuditLogService`, `RegisterOperationAndAuditService`, `RegisterOperationService` |
| `order` | `CancelOrderService`, `ConfirmOrderPaymentService`, `ConsultOrderService`, `CreateOrderService`, `LoadOrderForBuyerService` |
| `product` | `AddProductVariantService`, `ConsultProductService`, `CreateProductService`, `DiscontinueProductService`, `LoadManagedProductService`, `PublishProductService`, `SuspendProductService`, `UpdateProductPriceService` |
| `refund` | `CompleteRefundService`, `ConsultRefundService`, `IssueRefundService`, `LoadRefundForReviewService`, `ProcessRefundService`, `RejectRefundService` |
| `report` | `GenerateInventoryReportService`, `GenerateReturnsReportService`, `GenerateSalesReportService`, `ReportSupport` |
| `returns` | `ApproveReturnService`, `CompleteReturnService`, `ConsultReturnService`, `LoadReturnForReviewService`, `RejectReturnService`, `RequestReturnService` |
| `seller` | `ConsultSellerService`, `DeactivateSellerService`, `ReactivateSellerService`, `RegisterSellerService` |
| `shipment` | `ConfirmDeliveryService`, `ConsultShipmentService`, `CreateShipmentService`, `DispatchShipmentService`, `PrepareShipmentService`, `UpdateShipmentStatusService` |
| `support` | `AuthorizedService` |
| `user` | `ActivateUserService`, `BlockUserService`, `ChangeUserPasswordService`, `ChangeUserRoleService`, `ConsultUserService`, `CreateUserAccountService`, `LoadAuthenticatedUserService`, `LoginService`, `LogoutService`, `RegisterUserService` |
| `warehouse` | `ConsultWarehouseService`, `DeactivateWarehouseService`, `RegisterWarehouseService` |
