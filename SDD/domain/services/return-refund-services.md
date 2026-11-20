# Return and Refund Services

Devoluciones y reembolsos.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## RequestReturnService

Input Port: CU-13 request the return of an own delivered order (role BUYER).

- Puerto: `RequestReturnUseCase`
- Firma: `Return requestReturn(User actor, Order order, String reason)`

## ApproveReturnService

Input Port: CU-13 approve a return of an order with own products (role SELLER).

- Puerto: `ApproveReturnUseCase`
- Firma: `void approve(User actor, Return productReturn)`

## RejectReturnService

Input Port: CU-13 reject a return of an order with own products (role SELLER).

- Puerto: `RejectReturnUseCase`
- Firma: `void reject(User actor, Return productReturn)`

## CompleteReturnService

Input Port: CU-13 complete a return once goods are received back, restocking physical items (role LOGISTICS_OPERATOR).

- Puerto: `CompleteReturnUseCase`
- Firma: `void complete(User actor, Return productReturn)`

## ConsultReturnService

Input Port: CU-13 consult returns. BUYER sees its own, SELLER the ones of its products.

- Puerto: `ConsultReturnUseCase`
- Firma: `Return consultReturn(User actor, Return productReturn)`

## IssueRefundService

Input Port: CU-14 issue the refund of a refundable return (roles ADMINISTRATOR, SELLER).

- Puerto: `IssueRefundUseCase`
- Firma: `Refund issueRefund(User actor, Return productReturn, Money amount)`

## ProcessRefundService

Input Port: CU-14 start processing a refund (roles ADMINISTRATOR, SELLER).

- Puerto: `ProcessRefundUseCase`
- Firma: `void process(User actor, Refund refund)`

## CompleteRefundService

Input Port: CU-14 complete a refund (roles ADMINISTRATOR, SELLER).

- Puerto: `CompleteRefundUseCase`
- Firma: `void complete(User actor, Refund refund)`

## RejectRefundService

Input Port: CU-14 reject a refund (roles ADMINISTRATOR, SELLER).

- Puerto: `RejectRefundUseCase`
- Firma: `void reject(User actor, Refund refund)`

## ConsultRefundService

Input Port: CU-14 consult refunds. BUYER sees its own, SELLER the ones of its products.

- Puerto: `ConsultRefundUseCase`
- Firma: `Refund consultRefund(User actor, Refund refund)`

