# Report Services

Generación de reportes de ventas, inventario y devoluciones/reembolsos.

Patrón común: validar actor activo → validar rol → validar datos / recargar entidades → regla de negocio → persistir → registrar operación y auditoría.

## GenerateSalesReportService

Input Port: CU-15 sales report (roles SUPERVISOR, ADMINISTRATOR; read-only).

- Puerto: `GenerateSalesReportUseCase`
- Firma: `Report generateSalesReport(User actor, Map<String, Object> filters)`

## GenerateInventoryReportService

Input Port: CU-15 inventory report (roles SUPERVISOR, ADMINISTRATOR; read-only).

- Puerto: `GenerateInventoryReportUseCase`
- Firma: `Report generateInventoryReport(User actor, Map<String, Object> filters)`

## GenerateReturnsReportService

Input Port: CU-15 returns and refunds report (roles SUPERVISOR, ADMINISTRATOR; read-only).

- Puerto: `GenerateReturnsReportUseCase`
- Firma: `Report generateReturnsReport(User actor, Map<String, Object> filters)`

