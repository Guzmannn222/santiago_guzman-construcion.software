# Cart and Order Services

Carrito de compras y pedidos: creación, pago y cancelación.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## AddCartItemService

Input Port: CU-08 add a published product to the own cart (role BUYER).

- Puerto: `AddCartItemUseCase`
- Firma: `void addProduct(User actor, Cart cart, Product product, int quantity)`

## RemoveCartItemService

Input Port: CU-08 remove a product from the own cart (role BUYER).

- Puerto: `RemoveCartItemUseCase`
- Firma: `void removeProduct(User actor, Cart cart, Product product)`

## UpdateCartItemQuantityService

Input Port: CU-08 change the quantity of a cart item (role BUYER).

- Puerto: `UpdateCartItemQuantityUseCase`
- Firma: `void updateQuantity(User actor, Cart cart, Product product, int quantity)`

## ConsultCartService

Input Port: CU-08 consult a cart. A buyer only sees its own cart.

- Puerto: `ConsultCartUseCase`
- Firma: `Cart consultCart(User actor, Cart cart)`

## CreateOrderService

Input Port: CU-09 create an order from the own cart, reserving stock. {@code warehouse} may be null: the service picks one able to fulfill the order (role BUYER).

- Puerto: `CreateOrderUseCase`
- Firma: `Order createOrder(User actor, Cart cart, Warehouse warehouse)`

## ConfirmOrderPaymentService

Input Port: CU-09 confirm the payment of an own order (role BUYER).

- Puerto: `ConfirmOrderPaymentUseCase`
- Firma: `void confirmPayment(User actor, Order order)`

## CancelOrderService

Input Port: CU-09 cancel an own order before it ships, releasing the reserved stock (role BUYER).

- Puerto: `CancelOrderUseCase`
- Firma: `void cancelOrder(User actor, Order order)`

## ConsultOrderService

Input Port: CU-09 consult orders. BUYER sees its own, SELLER the ones with its products, the rest of the roles see all.

- Puerto: `ConsultOrderUseCase`
- Firma: `Order consultOrder(User actor, Order order)`

