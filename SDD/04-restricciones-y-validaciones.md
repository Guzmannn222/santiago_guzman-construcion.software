# 16. Restricciones Generales y Validaciones

## 16.1 Restricciones generales

| Código | Restricción |
|---|---|
| RG-01 | Toda operación debe ejecutarse por un usuario autenticado. |
| RG-02 | Cada usuario tendrá un único rol dentro del sistema. |
| RG-03 | Ningún participante podrá administrar información fuera de su rol. |

*Tabla 11. Restricciones generales del sistema.*

## 16.2 Validaciones críticas

| Elemento | Validación |
|---|---|
| Inventario | No se puede reservar inventario inexistente o marcado como "Dañado". |
| Pedidos | Un pedido finalizado no podrá ser modificado bajo ninguna circunstancia. |
| Usuarios | El documento de identidad y el correo electrónico deben ser únicos en la plataforma. |

*Tabla 12. Validaciones críticas explícitas de la especificación funcional.*

> Estas validaciones deben implementarse en la capa de servicios (`service/`) del proyecto Java —
> ver [`11-arquitectura-y-repositorio.md`](./11-arquitectura-y-repositorio.md) — y no duplicarse en
> controladores ni repositorios.
