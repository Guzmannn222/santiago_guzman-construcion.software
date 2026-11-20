# Domain Value Objects

## Introducción

Objetos de valor del dominio (`application.domain.valueobjects`). Son inmutables y se comparan por valor. Los catálogos cerrados extienden `DomainCatalog` (código estable + `fromCode`, `values()`), de modo que el vocabulario del negocio no depende de `enum` de infraestructura.

## Currency

Currency in which a Money amount is expressed.

Valores: `COP`, `USD`, `EUR`

## EntityReference

Value Object identifying the domain entity affected by an Operation or recorded in an AuditLog (the marketplace equivalent of the reference project's polymorphic "affected product").

Atributos: `type`, `identifier`

- `EntityReference of(EntityType type, Long id)`
- `String toString()`

## EntityType

Kind of domain entity referenced by an Operation or AuditLog.

Valores: `USER`, `BUYER`, `SELLER`, `WAREHOUSE`, `PRODUCT`, `INVENTORY`, `CART`, `ORDER`, `INVOICE`, `SHIPMENT`, `RETURN`, `REFUND`, `REPORT`

## InventoryMovementType

Types of movement recorded against an Inventory record.

Valores: `INBOUND`, `RESERVATION`, `SALE_OUTBOUND`, `ADJUSTMENT`, `RETURN`

## InventoryStatus

Status of an Inventory record. DAMAGED stock can never be reserved for a sale.

Valores: `AVAILABLE`, `LOW_STOCK`, `OUT_OF_STOCK`, `DAMAGED`

## Money

Value Object representing an amount of money in a specific currency.  <p>Invariants: the amount and the currency are always present, the amount is never negative and it is always normalized to two decimals ({@link RoundingMode#HALF_UP}). Operations that would mix currencies are rejected. Strictly-positive amounts (prices, refunds) are enforced by the aggregate that owns the amount through {@link #isPositive()}.

Valores: `SCALE`

## OperationType

Significant business action recorded in the operations log and in the audit trail.

Valores: `USER_REGISTRATION`, `USER_LOGIN`, `USER_LOGOUT`, `USER_PASSWORD_CHANGE`, `USER_BLOCKING`, `USER_ACTIVATION`, `USER_ROLE_CHANGE`, `BUYER_REGISTRATION`, `BUYER_ADDRESS_ADDED`, `SELLER_REGISTRATION`, `SELLER_DEACTIVATION`, `SELLER_REACTIVATION`, `WAREHOUSE_REGISTRATION`, `WAREHOUSE_DEACTIVATION`, `PRODUCT_CREATION`, `PRODUCT_VARIANT_ADDED`, `PRODUCT_PRICE_UPDATE`, `PRODUCT_PUBLICATION`, `PRODUCT_SUSPENSION`, `PRODUCT_DISCONTINUATION`, `INVENTORY_OPENING`, `STOCK_RECEIVED`, `STOCK_RESERVED`, `STOCK_ADJUSTED`, `STOCK_RETURNED`, `INVENTORY_DAMAGED`, `CART_ITEM_ADDED`, `CART_ITEM_REMOVED`, `CART_ITEM_UPDATED`, `ORDER_CREATION`, `ORDER_PAYMENT`, `ORDER_CANCELLATION`, `INVOICE_GENERATION`, `INVOICE_CANCELLATION`, `SHIPMENT_CREATION`, `SHIPMENT_PREPARATION`, `SHIPMENT_DISPATCH`, `SHIPMENT_STATUS_UPDATE`, `DELIVERY_CONFIRMATION`, `RETURN_REQUEST`, `RETURN_APPROVAL`, `RETURN_REJECTION`, `RETURN_COMPLETION`, `REFUND_ISSUE`, `REFUND_PROCESSING`, `REFUND_COMPLETION`, `REFUND_REJECTION`, `REPORT_GENERATION`

## OrderStatus

Lifecycle of an Order: PENDING_PAYMENT -> PAID -> SHIPPED -> DELIVERED -> FINISHED (or CANCELLED before shipping).

Valores: `CART`, `PENDING_PAYMENT`, `PAID`, `SHIPPED`, `DELIVERED`, `FINISHED`, `CANCELLED`

## ProductStatus

Publication status of a Product in the catalog.

Valores: `PUBLISHED`, `SUSPENDED`, `DISCONTINUED`

## ProductType

Distinguishes physical goods (inventory + shipping) from digital goods (delivered right after payment).

Valores: `PHYSICAL`, `DIGITAL`

## RefundStatus

Status of a Refund.

Valores: `PENDING`, `PROCESSING`, `COMPLETED`, `REJECTED`

## ReportType

Kind of consolidated administrative report.

Valores: `SALES`, `INVENTORY`, `RETURNS_AND_REFUNDS`

## ReturnStatus

Status of a post-sale Return request.

Valores: `REQUESTED`, `APPROVED`, `REJECTED`, `COMPLETED`

## ShipmentStatus

Logistics status of a Shipment.

Valores: `PENDING`, `PREPARING`, `DISPATCHED`, `IN_TRANSIT`, `DELIVERED`

## SystemRole

Business role of a platform participant. Each user has exactly one role (RG-02).

Valores: `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`

## TradingStatus

Commercial status of a Buyer; only ACTIVE buyers can place orders.

Valores: `ACTIVE`, `PENDING_APPROVAL`, `SUSPENDED`, `BLOCKED`

## UserStatus

Operating status of a User (independent from the commercial status of a Buyer).

Valores: `ACTIVE`, `INACTIVE`, `BLOCKED`

## WarehouseType

Distinguishes marketplace-owned warehouses from seller-owned warehouses.

Valores: `MARKETPLACE`, `SELLER`

