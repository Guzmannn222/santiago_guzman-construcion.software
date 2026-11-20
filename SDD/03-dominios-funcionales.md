# 7–15. Dominios Funcionales del Negocio

Los dominios funcionales se derivan de los procesos y componentes definidos en la especificación
de negocio. Esta separación permite convertir la narrativa funcional en agrupaciones coherentes
de clases y responsabilidades.

| Dominio | Propósito |
|---|---|
| D1 — Administración de Usuarios | Identificación y estado de usuarios. |
| D2 — Gestión de Compradores | Datos y participación comercial. |
| D3 — Gestión de Vendedores | Incorporación y mantenimiento de proveedores. |
| D4 — Gestión de Bodegas | Espacios físicos de almacenamiento. |
| D5 — Gestión del Catálogo | Productos, variantes y publicación. |
| D6 — Gestión del Inventario | Existencias y movimientos. |
| D7 — Gestión de Pedidos | Carrito y ciclo comercial. |
| D8 — Facturación | Información comercial de venta. |
| D9 — Logística | Preparación, despacho y entrega. |
| D10 — Posventa | Devoluciones y reembolsos. |
| D11 — Reportes | Consulta administrativa. |

*Tabla 6. Dominios funcionales identificados a partir de la especificación de negocio.*

## D1 — Administración de Usuarios

Constituye la base de identificación del Marketplace. Cada usuario cuenta con identificador,
nombre, correo, rol y estado; el rol determina sus responsabilidades y permisos dentro del sistema.

| Atributo | Descripción | Obligatorio | Restricción |
|---|---|---|---|
| Identificador | Identifica de forma única al usuario. | Sí | Único |
| Nombre completo | Nombre oficial del usuario. | Sí | No vacío |
| Correo electrónico | Medio principal de acceso y comunicación. | Sí | Único |
| Rol | Define responsabilidades y permisos. | Sí | Catálogo cerrado |
| Estado | Condición operativa del usuario. | Sí | Catálogo cerrado |

*Tabla 7. Atributos del dominio Administración de Usuarios.*

## D2 — Gestión de Compradores

El comprador participa en los procesos comerciales del sistema y, conforme a la regla de negocio,
**no administra información de otros compradores ni de inventarios**. La dirección principal es
obligatoria; las direcciones adicionales son opcionales; el estado comercial es obligatorio en
todos los casos.

| Atributo | Descripción | Obligatorio |
|---|---|---|
| Dirección principal | Ubicación habitual para entregas. | Sí |
| Direcciones adicionales | Ubicaciones secundarias de entrega. | No |
| Estado comercial | Condición para realizar compras. | Sí |

*Tabla 8. Atributos del dominio Gestión de Compradores.*

## D3 — Gestión de Vendedores

Su objetivo es administrar la incorporación y el mantenimiento de proveedores de productos.

**Regla de negocio:** los vendedores no pueden auto-registrarse; son incorporados exclusivamente
por el Administrador (véase CU-03 en [`05-casos-de-uso.md`](./05-casos-de-uso.md)).

## D4 — Gestión de Bodegas

Controla los espacios físicos de almacenamiento y distingue entre bodegas del Marketplace y
bodegas propias de los Vendedores, clasificación que se modela mediante la enumeración
`TipoBodega`.

## D5 — Gestión del Catálogo

El catálogo diferencia entre productos físicos, que requieren inventario y despacho, y productos
digitales, que se entregan inmediatamente después del pago. También contempla variantes de
producto y tres estados de publicación: Publicado, Suspendido y Descontinuado.

| Atributo | Descripción | Tipo de dato |
|---|---|---|
| Tipo de producto | Físico o Digital. | Selección (enum) |
| Variantes | Diferencias de color, talla, modelo, etc. | Lista |
| Estado | Publicado, Suspendido o Descontinuado. | Selección (enum) |

*Tabla 9. Atributos del dominio Gestión del Catálogo.*

## D6 — Gestión del Inventario

El inventario es distribuido y debe estar vinculado obligatoriamente a un producto y a una bodega
específica. Los movimientos contemplados son: Ingreso, Reserva, Salida por venta, Ajuste y
Devolución.

**Restricción crítica:** no se permiten existencias negativas bajo ninguna circunstancia.

## D7 — Gestión de Pedidos

El pedido representa el compromiso comercial formal entre comprador y vendedor, y constituye el
proceso central del sistema. Su ciclo de vida sigue una secuencia estricta de estados:

**Carrito → Pendiente de Pago → Pagado → Despachado → Entregado / Finalizado**

## D8–D11

| Dominio | Descripción |
|---|---|
| D8 — Facturación | Registra la información comercial asociada a las ventas. |
| D9 — Logística | Gestiona los procesos logísticos de productos físicos. |
| D10 — Posventa | Administra devoluciones y reembolsos. |
| D11 — Reportes | Consolida información administrativa para consulta. |

*Tabla 10. Descripción de los dominios funcionales D8 a D11.*
