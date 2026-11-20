# Buyer Services

Alta y consulta del perfil de comprador y de sus direcciones.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## RegisterBuyerService

Input Port: CU-02 create the buyer profile (and cart) of the authenticated BUYER user.

- Puerto: `RegisterBuyerUseCase`
- Firma: `Buyer registerBuyer(User actor, String primaryAddress)`

## AddBuyerAddressService

Input Port: CU-02 add a delivery address to the actor own buyer profile.

- Puerto: `AddBuyerAddressUseCase`
- Firma: `void addAddress(User actor, Buyer buyer, String address)`

## ConsultBuyerService

Input Port: CU-02 consult buyers. A buyer only sees its own profile.

- Puerto: `ConsultBuyerUseCase`
- Firma: `Buyer consultBuyer(User actor, Buyer buyer)`

