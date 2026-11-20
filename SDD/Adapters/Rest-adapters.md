# REST Adapters

Paquete `application.adapters.rest`: `controllers`, `dtos` (request/response), `mappers` y `exception`.

- Un controlador por rol, delgado: recibe el DTO validado, lo mapea al modelo de dominio con un mapper, delega en la fachada de rol y mapea la respuesta.
- Los controladores **no** contienen lógica de negocio ni acceden a repositorios.
- El usuario autenticado se obtiene del `SecurityContext` (JWT), nunca de un header enviado por el cliente (se elimina el antiguo `X-Actor-Id`).
- Rutas bajo `/api/v1`: `/auth`, `/buyer`, `/seller`, `/logistics-operator`, `/administrator`, `/supervisor`.
