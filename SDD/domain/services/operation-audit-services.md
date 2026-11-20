# Operation and Audit Services

Trazabilidad: operaciones (MySQL) y bitácora de auditoría (MongoDB).

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## ConsultAuditLogsService

Input Port: consult the audit trail (roles SUPERVISOR, ADMINISTRATOR).

- Puerto: `ConsultAuditLogsUseCase`
- Firma: `List<AuditLog> consultByUser(User actor, User user)`

## ConsultOperationsService

Input Port: consult the operations log (roles SUPERVISOR, ADMINISTRATOR).

- Puerto: `ConsultOperationsUseCase`
- Firma: `List<Operation> consultByUser(User actor, User user)`

