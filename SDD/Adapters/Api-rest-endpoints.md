# API REST Endpoints

Prefijo `/api/v1`. Autenticación: `Authorization: Bearer <JWT>` (salvo `/auth`).

| Grupo | Ruta base | Rol | Casos de uso |
|---|---|---|---|
| Acceso público | `/auth` | — | login, logout, registro de comprador |
| Comprador | `/buyer` | BUYER | perfil, direcciones, catálogo, carrito, pedidos, pago, cancelación, facturas, envíos, devoluciones, reembolsos |
| Vendedor | `/seller` | SELLER | productos, variantes, precio, publicación, inventario |
| Operador logístico | `/logistics-operator` | LOGISTICS_OPERATOR | bodegas, stock, envíos, entrega |
| Administrador | `/administrator` | ADMINISTRATOR | usuarios, roles, vendedores, reembolsos, reportes |
| Supervisor | `/supervisor` | SUPERVISOR | consulta de operaciones, auditoría y reportes |

Los métodos de cada rol están definidos en las fachadas `BuyerPort`, `SellerPort`, `LogisticsOperatorPort`, `AdministratorPort`, `SupervisorPort` y `PublicAccessPort` (ver [Input-ports](../domain/Input-ports.md)).

Códigos HTTP: 200/201/204 éxito, 400 validación, 401 credenciales inválidas, 403 sin permiso/token obsoleto, 404 no encontrado, 409 regla de negocio.

## Reportes por vendedor y regla de stock en el carrito

- `GET /seller/reports/sales|inventory|returns`: el vendedor consulta solo sus propios datos (ventas, inventario con movimientos, devoluciones y reembolsos).
- `GET /administrator/reports/*?sellerId=` y `GET /supervisor/reports/*?sellerId=`: consulta del reporte de un vendedor concreto.
- `POST /buyer/cart/items` y `PUT /buyer/cart/items/{productId}`: si la cantidad total de un producto físico supera el stock disponible responde 409 (`InsufficientStockException`).
- Devoluciones parciales: `POST /buyer/orders/{id}/returns` acepta `items:[{productId, quantity}]`; solo el vendedor dueño del producto devuelto puede aprobar o reembolsar.
