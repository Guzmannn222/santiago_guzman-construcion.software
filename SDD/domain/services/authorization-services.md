# Authorization Services

## ValidateUserAuthorizationStatusService

Garantiza que el usuario que ejecuta la operación exista y esté en estado `ACTIVE` (`User.validateAccess()`). En caso contrario lanza `UnauthorizedOperationException` (HTTP 403).

## ValidateRoleAuthorizationService

`execute(User actor, SystemRole... allowedRoles)`: verifica que el rol del actor esté entre los permitidos para el servicio (`BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`). Lanza `UnauthorizedOperationException` si no lo está.

## LoadAuthenticatedUserService

Carga el usuario persistido referenciado por el claim `sub` del JWT. Es dominio puro: el adaptador de seguridad construye el principal de Spring a partir de su resultado.

## Reglas de propiedad

Además del rol, los servicios verifican propiedad del recurso (`Buyer.isOwnedBy`, `Order.isOwnedBy`, `Product.belongsTo`, `Order.containsProductOf`) para que un comprador solo opere sobre sus propios recursos y un vendedor solo sobre los suyos.
