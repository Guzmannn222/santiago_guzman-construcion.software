# 21–23. Clases del Dominio, Atributos, Métodos y Enumeraciones

## 21. Identificación de Clases del Dominio

A partir de los conceptos funcionales descritos en los dominios de negocio se identifican
diecisiete clases candidatas para el modelo orientado a objetos. Estas clases constituyen una
propuesta de diseño para Java, derivada directamente de los dominios de negocio y no representan
requisitos adicionales.

| Clase | Responsabilidad | Atributos principales |
|---|---|---|
| Usuario | Representar la identidad y rol del participante. | id, nombre, correo, rol, estado |
| Comprador | Representar al usuario comprador y sus direcciones. | id, direccionPrincipal, direccionesAdicionales, estado |
| Vendedor | Representar al proveedor de productos. | id, datosComerciales, estado |
| Bodega | Representar un espacio de almacenamiento. | id, ubicacion, tipo, estado |
| Producto | Representar un bien físico o digital. | id, nombre, descripcion, precio, tipo, estado |
| Variante | Representar diferencias del producto. | id, nombre, valor |
| Inventario | Controlar existencias por producto y bodega. | producto, bodega, existencias, estado |
| MovimientoInventario | Registrar cambios de existencias. | tipo, cantidad, fecha, motivo |
| Carrito | Representar la selección provisional de compra. | id, comprador, items, total |
| DetalleCarrito | Representar un producto dentro del carrito. | producto, cantidad, precio |
| Pedido | Representar la compra formal. | id, comprador, total, estado, fecha |
| DetallePedido | Representar cada línea del pedido. | producto, cantidad, precioUnitario, subtotal |
| Factura | Representar la información comercial de la venta. | id, pedido, subtotal, impuestos, total |
| Envio | Representar la logística física del pedido. | id, pedido, direccion, estado, fechas |
| Devolucion | Representar la solicitud de posventa. | id, pedido, motivo, estado |
| Reembolso | Representar la devolución de dinero. | id, devolucion, monto, estado |
| Reporte | Representar una consulta administrativa. | id, tipo, filtros, fechaGeneracion |

*Tabla 15. Clases candidatas del modelo de dominio.*

## 22. Atributos y Métodos Propuestos

| Clase | Métodos principales |
|---|---|
| Usuario | `activar()`, `bloquear()`, `cambiarRol()`, `validarAcceso()` |
| Comprador | `agregarDireccion()`, `gestionarCarrito()`, `crearPedido()` |
| Vendedor | `registrarProducto()`, `actualizarProducto()`, `publicarProducto()`, `suspenderProducto()` |
| Bodega | `registrarInventario()`, `consultarExistencias()` |
| Producto | `agregarVariante()`, `publicar()`, `suspender()`, `actualizarPrecio()` |
| Inventario | `ingresar()`, `reservar()`, `retirar()`, `ajustar()`, `devolver()` |
| MovimientoInventario | `registrarMovimiento()` |
| Carrito | `agregarProducto()`, `quitarProducto()`, `actualizarCantidad()`, `calcularTotal()`, `confirmar()` |
| Pedido | `confirmarPago()`, `cambiarEstado()`, `puedeModificar()`, `finalizar()` |
| DetallePedido | `calcularSubtotal()` |
| Factura | `generar()`, `calcularTotal()`, `anular()` |
| Envio | `preparar()`, `despachar()`, `actualizarEstado()`, `confirmarEntrega()` |
| Devolucion | `solicitar()`, `aprobar()`, `rechazar()`, `completar()` |
| Reembolso | `procesar()`, `completar()`, `rechazar()` |
| Reporte | `generar()`, `filtrar()`, `exportar()` |

*Tabla 16. Métodos principales propuestos por clase.*

Estas firmas se implementan como esqueletos en
[`../nexusmarket/src/main/java/com/nexusmarket/model`](../nexusmarket/src/main/java/com/nexusmarket/model).

## 23. Enumeraciones Propuestas

Las enumeraciones formalizan los catálogos cerrados de valores mencionados en la especificación
de negocio (roles, estados y tipos), evitando el uso de cadenas de texto libres en el modelo de
datos.

| Enumeración | Valores |
|---|---|
| Rol | COMPRADOR, VENDEDOR, OPERADOR_LOGISTICO, ADMINISTRADOR, SUPERVISOR |
| EstadoUsuario | ACTIVO, BLOQUEADO, INACTIVO |
| TipoProducto | FISICO, DIGITAL |
| EstadoProducto | PUBLICADO, SUSPENDIDO, DESCONTINUADO |
| TipoBodega | MARKETPLACE, VENDEDOR |
| TipoMovimientoInventario | INGRESO, RESERVA, SALIDA_VENTA, AJUSTE, DEVOLUCION |
| EstadoPedido | CARRITO, PENDIENTE_PAGO, PAGADO, DESPACHADO, ENTREGADO, FINALIZADO |
| EstadoEnvio | PENDIENTE, PREPARANDO, DESPACHADO, EN_TRANSITO, ENTREGADO |
| EstadoDevolucion | SOLICITADA, APROBADA, RECHAZADA, COMPLETADA |
| EstadoReembolso | PENDIENTE, PROCESANDO, COMPLETADO, RECHAZADO |

*Tabla 17. Enumeraciones propuestas para el modelo de dominio.*

> **Nota:** en la versión 2.0 de este documento la enumeración de movimientos de inventario se
> renombra a `TipoMovimientoInventario` para evitar la colisión de nombre con la clase
> `MovimientoInventario` del dominio.

Implementadas en
[`../nexusmarket/src/main/java/com/nexusmarket/enums`](../nexusmarket/src/main/java/com/nexusmarket/enums).
