# Use Cases Adapters

Paquete `application.adapters.useCases`: una clase `*UseCaseImpl` por fachada de rol que implementa el puerto de entrada correspondiente y **delega** en los servicios de dominio (sin lógica propia):

- `AdministratorPort`
- `BuyerPort`
- `LogisticsOperatorPort`
- `PublicAccessPort`
- `SellerPort`
- `SupervisorPort`

Cada método delega al servicio de dominio de su caso de uso, que valida actor, rol y reglas, y registra operación y auditoría.
