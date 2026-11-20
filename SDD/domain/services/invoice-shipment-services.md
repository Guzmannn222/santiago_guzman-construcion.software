# Invoice and Shipment Services

Facturación y logística de envíos.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## GenerateInvoiceService

Input Port: CU-10 generate the invoice of a paid order (role ADMINISTRATOR). A null tax rate uses the configured default.

- Puerto: `GenerateInvoiceUseCase`
- Firma: `Invoice generateInvoice(User actor, Order order, BigDecimal taxRate)`

## CancelInvoiceService

Input Port: CU-10 cancel an invoice (role ADMINISTRATOR).

- Puerto: `CancelInvoiceUseCase`
- Firma: `void cancelInvoice(User actor, Invoice invoice)`

## ConsultInvoiceService

Input Port: CU-10 consult invoices. BUYER only sees invoices of its own orders.

- Puerto: `ConsultInvoiceUseCase`
- Firma: `Invoice consultInvoice(User actor, Invoice invoice)`

## CreateShipmentService

Input Port: CU-11 create the shipment of a paid order with physical items (role LOGISTICS_OPERATOR). A null address uses the buyer primary address.

- Puerto: `CreateShipmentUseCase`
- Firma: `Shipment createShipment(User actor, Order order, String address)`

## PrepareShipmentService

Input Port: CU-11 prepare a shipment (role LOGISTICS_OPERATOR).

- Puerto: `PrepareShipmentUseCase`
- Firma: `void prepare(User actor, Shipment shipment)`

## DispatchShipmentService

Input Port: CU-11 dispatch a shipment (role LOGISTICS_OPERATOR).

- Puerto: `DispatchShipmentUseCase`
- Firma: `void dispatch(User actor, Shipment shipment)`

## UpdateShipmentStatusService

Input Port: CU-11 update the tracking status of a shipment (role LOGISTICS_OPERATOR).

- Puerto: `UpdateShipmentStatusUseCase`
- Firma: `void updateStatus(User actor, Shipment shipment, ShipmentStatus status)`

## ConfirmDeliveryService

Input Port: CU-12 confirm the delivery of a shipment, finishing its order (role LOGISTICS_OPERATOR).

- Puerto: `ConfirmDeliveryUseCase`
- Firma: `void confirmDelivery(User actor, Shipment shipment)`

## ConsultShipmentService

Input Port: CU-11/CU-12 consult shipments. BUYER only sees shipments of its own orders.

- Puerto: `ConsultShipmentUseCase`
- Firma: `Shipment consultShipment(User actor, Shipment shipment)`

