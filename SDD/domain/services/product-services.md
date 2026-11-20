# Product Services

Ciclo de vida del producto: creación, variantes, precio, publicación, suspensión y descontinuación.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## CreateProductService

Input Port: CU-05 create a product (role SELLER). {@code seller} may be null or a stub: the service resolves the actor own seller profile and rejects any other seller.

- Puerto: `CreateProductUseCase`
- Firma: `Product createProduct(User actor, Seller seller, String name, String description, Money price, ProductType type)`

## AddProductVariantService

Input Port: CU-05 add a variant to an own product (role SELLER).

- Puerto: `AddProductVariantUseCase`
- Firma: `void addVariant(User actor, Product product, String name, String value)`

## UpdateProductPriceService

Input Port: CU-05 update the price of an own product (role SELLER).

- Puerto: `UpdateProductPriceUseCase`
- Firma: `void updatePrice(User actor, Product product, Money newPrice)`

## PublishProductService

Input Port: CU-06 publish an own product (role SELLER).

- Puerto: `PublishProductUseCase`
- Firma: `void publish(User actor, Product product)`

## SuspendProductService

Input Port: CU-06 suspend an own product (role SELLER).

- Puerto: `SuspendProductUseCase`
- Firma: `void suspend(User actor, Product product)`

## DiscontinueProductService

Input Port: CU-06 discontinue an own product (role SELLER).

- Puerto: `DiscontinueProductUseCase`
- Firma: `void discontinue(User actor, Product product)`

## ConsultProductService

Input Port: CU-05/CU-06 consult products. Published products are visible to any authenticated user.

- Puerto: `ConsultProductUseCase`
- Firma: `Product consultProduct(User actor, Product product)`

