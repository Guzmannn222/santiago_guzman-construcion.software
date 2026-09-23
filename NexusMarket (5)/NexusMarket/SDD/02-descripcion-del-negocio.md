# 4–6. Descripción General, Participantes y Modelo Operativo

## 4. Descripción General del Marketplace

La organización facilita la comercialización de productos de terceros mediante una plataforma
empresarial unificada. La operación centraliza la administración de usuarios, vendedores,
compradores, bodegas, productos, inventario, pedidos, facturación y envíos.

| Componente | Descripción |
|---|---|
| Usuarios | Personas autorizadas para interactuar con el sistema. |
| Vendedores | Responsables de comercializar productos. |
| Compradores | Usuarios que realizan compras. |
| Bodegas | Lugares donde se administra el inventario físico. |
| Productos | Bienes físicos o digitales ofrecidos. |
| Inventario | Existencias disponibles para comercialización. |
| Pedidos | Solicitudes de compra realizadas por compradores. |
| Facturación | Información comercial asociada a las ventas. |
| Envíos | Procesos logísticos para productos físicos. |

*Tabla 3. Componentes principales del negocio.*

## 5. Participantes del Negocio

Cada participante desempeña un único rol dentro del sistema y únicamente puede interactuar con la
información correspondiente a sus funciones (véase la restricción RG-03 en
[`04-restricciones-y-validaciones.md`](./04-restricciones-y-validaciones.md)).

| Participante | Descripción general |
|---|---|
| Comprador | Persona que adquiere productos publicados en el catálogo. |
| Vendedor | Responsable de registrar y administrar sus productos. |
| Operador Logístico | Encargado de la operación física de bodegas y despachos. |
| Administrador | Responsable de la administración de vendedores y bodegas. |
| Supervisor | Perfil de consulta y seguimiento operativo. |

*Tabla 4. Participantes del negocio y su rol funcional.*

## 6. Modelo Operativo del Negocio

El ciclo de negocio definido por NexusMarket sigue ocho etapas secuenciales, desde la
incorporación del vendedor hasta el cierre del pedido tras la entrega confirmada:

1. **Incorporación** — El Administrador registra al vendedor y su primera bodega.
2. **Catálogo** — El vendedor registra productos y define sus características.
3. **Inventario** — Se registran existencias iniciales en las bodegas asociadas.
4. **Publicación** — Los productos se hacen visibles en el catálogo público.
5. **Compra** — El comprador selecciona productos mediante el carrito y confirma el pedido.
6. **Transacción** — Se valida el pago y se inicia la preparación del pedido.
7. **Logística** — Se realiza el empaque, despacho y transporte del pedido.
8. **Cierre** — El pedido se marca como finalizado tras la entrega confirmada.

*Tabla 5. Etapas del modelo operativo del negocio.*
