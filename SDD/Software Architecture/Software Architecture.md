# Software Architecture

NexusMarket usa **arquitectura hexagonal** (DDD + Ports & Adapters) sobre Java 17 y Spring Boot 4.1.

```text
application/
├── domain/            # Núcleo puro, sin Spring ni JPA
│   ├── models/  valueobjects/  enums/  exceptions/
│   ├── ports/in/      # casos de uso + fachadas por rol
│   ├── ports/out/     # repositorios, JWT, password, notificación, configuración
│   └── services/      # authorization, user, buyer, seller, warehouse, product,
│                      # inventory, cart, order, invoice, shipment, returns,
│                      # refund, report, operation
├── adapters/
│   ├── rest/          # controllers, dtos, mappers, exception
│   ├── useCases/      # implementan los puertos de entrada delegando en servicios
│   ├── persistence/   # jpa (MySQL) y mongodb (auditoría)
│   ├── security/      # JWT, filtro, BCrypt, configuración de Spring Security
│   ├── config/        # beans y configuración de negocio
│   └── notification/  # implementación de NotificationPort
└── infrastructure/
    └── seed/          # DatabaseSeeder (sin lógica de negocio)
```

## Reglas de dependencia

1. `domain` no depende de `adapters` ni `infrastructure`.
2. Los adaptadores dependen de los puertos del dominio.
3. Los controladores solo conocen puertos de entrada; los servicios solo conocen puertos de salida.
4. Cada operación de escritura registra operación (MySQL) y auditoría (MongoDB).

## Seguridad

JWT stateless con `sub`, `jti`, `ver`; rol y estado se leen siempre de la base de datos. BCrypt para contraseñas. CORS por `FRONTEND_ORIGIN`.

## Persistencia

MySQL 8 (JPA, DDL automático) para el modelo transaccional y MongoDB 6 para la auditoría.
