# 30–31. Matriz de Responsabilidades y Consideraciones de Implementación

## 30. Matriz de Responsabilidades

La matriz de responsabilidades mantiene la distribución funcional establecida en la
especificación de negocio, y sirve como referencia directa para definir los permisos de acceso
por rol en la capa de servicios.

| Proceso | Comprador | Vendedor | Op. Logístico | Administrador | Supervisor |
|---|---|---|---|---|---|
| Registro de vendedores | — | — | — | ✔ | — |
| Registro de productos | — | ✔ | — | — | — |
| Administración de inventario | — | ✔ | ✔ | — | — |
| Gestión de pedidos | ✔ | ✔ | ✔ | — | ✔ |
| Gestión de reembolsos | ✔ | ✔ | — | ✔ | — |
| Consulta administrativa | — | — | ✔ | ✔ | ✔ |

*Tabla 20. Matriz de responsabilidades por rol y proceso.*

## 31. Consideraciones para la Implementación

Se recomienda implementar el código Java siguiendo un orden incremental que respete las
dependencias entre capas:

1. Primero, las entidades del dominio y las enumeraciones (paquetes `model/` y `enums/`).
2. Segundo, los repositorios de persistencia (paquete `repository/`).
3. Tercero, los servicios con las reglas de negocio y validaciones (paquete `service/`).
4. Finalmente, los controladores o el menú de interacción definidos para la entrega académica
   (paquete `controller/`).

Las validaciones de inventario, estados y permisos deben mantenerse centralizadas en la capa de
servicios, evitando su duplicación en controladores o repositorios.

### 31.1 Consideraciones no funcionales sugeridas

| Aspecto | Recomendación |
|---|---|
| Integridad de datos | Validar en la capa de servicio que el correo y el documento de identidad sean únicos antes de persistir un Usuario. |
| Concurrencia de inventario | Sincronizar las operaciones de reserva/retiro de Inventario para evitar condiciones de carrera que generen existencias negativas. |
| Trazabilidad | Registrar cada cambio de estado de Pedido y Envio con fecha y usuario responsable, para auditoría posterior. |
| Manejo de errores | Utilizar excepciones de dominio (p. ej. `StockInsuficienteException`) en lugar de códigos de retorno genéricos. |
| Pruebas | Cubrir con pruebas unitarias las reglas críticas: existencias negativas, pedidos finalizados inmodificables y unicidad de usuario. |

*Tabla 21. Consideraciones no funcionales sugeridas para la implementación.*
