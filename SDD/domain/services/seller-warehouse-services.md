# Seller and Warehouse Services

Gestión de vendedores y bodegas.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## RegisterSellerService

Input Port: CU-03 onboard a seller together with its first warehouse (role ADMINISTRATOR).

- Puerto: `RegisterSellerUseCase`
- Firma: `Seller registerSeller(User actor, User sellerUser, String businessInfo, String firstWarehouseLocation)`

## DeactivateSellerService

Input Port: CU-03 deactivate a seller (role ADMINISTRATOR).

- Puerto: `DeactivateSellerUseCase`
- Firma: `void deactivate(User actor, Seller seller)`

## ReactivateSellerService

Input Port: CU-03 reactivate a seller (role ADMINISTRATOR).

- Puerto: `ReactivateSellerUseCase`
- Firma: `void reactivate(User actor, Seller seller)`

## ConsultSellerService

Input Port: CU-03 consult sellers. A seller only sees its own profile.

- Puerto: `ConsultSellerUseCase`
- Firma: `Seller consultSeller(User actor, Seller seller)`

## RegisterWarehouseService

Input Port: CU-04 register a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR).

- Puerto: `RegisterWarehouseUseCase`
- Firma: `Warehouse registerWarehouse(User actor, String location, WarehouseType type, Seller owner)`

## DeactivateWarehouseService

Input Port: CU-04 deactivate a warehouse (roles ADMINISTRATOR, LOGISTICS_OPERATOR).

- Puerto: `DeactivateWarehouseUseCase`
- Firma: `void deactivateWarehouse(User actor, Warehouse warehouse)`

## ConsultWarehouseService

Input Port: CU-04 consult warehouses.

- Puerto: `ConsultWarehouseUseCase`
- Firma: `Warehouse consultWarehouse(User actor, Warehouse warehouse)`

