# Inventory Services

Apertura, recepción, reserva, ajuste, devolución y marcado de daño de inventario.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## OpenInventoryService

Input Port: CU-07 open an inventory record for a physical product in a warehouse (roles SELLER, LOGISTICS_OPERATOR).

- Puerto: `OpenInventoryUseCase`
- Firma: `Inventory openInventory(User actor, Product product, Warehouse warehouse, int initialStock)`

## ReceiveStockService

Input Port: CU-07 receive stock (roles SELLER, LOGISTICS_OPERATOR).

- Puerto: `ReceiveStockUseCase`
- Firma: `void receiveStock(User actor, Inventory inventory, int quantity, String reason)`

## ReserveStockService

Input Port: CU-07 reserve stock for a pending order (role BUYER, from checkout).

- Puerto: `ReserveStockUseCase`
- Firma: `void reserveStock(User actor, Inventory inventory, int quantity, String reason)`

## AdjustStockService

Input Port: CU-07 set the absolute stock after a physical count (roles SELLER, LOGISTICS_OPERATOR).

- Puerto: `AdjustStockUseCase`
- Firma: `void adjustStock(User actor, Inventory inventory, int newQuantity, String reason)`

## ReturnStockService

Input Port: CU-07 return units to the warehouse (roles LOGISTICS_OPERATOR, SELLER).

- Puerto: `ReturnStockUseCase`
- Firma: `void returnStock(User actor, Inventory inventory, int quantity, String reason)`

## MarkInventoryDamagedService

Input Port: CU-07 mark an inventory record as damaged (roles SELLER, LOGISTICS_OPERATOR).

- Puerto: `MarkInventoryDamagedUseCase`
- Firma: `void markDamaged(User actor, Inventory inventory)`

## CheckStockAvailabilityService

Input Port: CU-07 check whether a quantity can be reserved (role BUYER).

- Puerto: `CheckStockAvailabilityUseCase`
- Firma: `boolean checkAvailability(User actor, Inventory inventory, int quantity)`

## ConsultInventoryService

Input Port: CU-07 consult inventory. SELLER only sees inventory of its own products or warehouses.

- Puerto: `ConsultInventoryUseCase`
- Firma: `Inventory consultInventory(User actor, Inventory inventory)`

