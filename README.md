# NexusMarket — Construcción de Software 2 (2026-2)

Marketplace en **Java 17 + Spring Boot 4.1** con **arquitectura hexagonal** (DDD + Ports & Adapters), construido a partir de la especificación del `SDD/`. Incluye roles de negocio, JWT con invalidación de sesiones, persistencia relacional en **MySQL** y auditoría en **MongoDB**.

> **Para poner el proyecto en marcha paso a paso, ver [`SETUP.md`](SETUP.md).**

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje / Build | Java 17, Maven (wrapper `nexusmarket/mvnw` o contenedor Docker) |
| Framework | Spring Boot 4.1.0, Spring MVC, Spring Security (JWT stateless, BCrypt) |
| Persistencia relacional | Spring Data JPA / Hibernate → MySQL 8 (`nexusmarket_db`, puerto 3306, DDL automático) |
| Auditoría NoSQL | Spring Data MongoDB → MongoDB (`audit_db.audit_logs`, puerto 27017) |
| JWT | JJWT 0.12.6 (`sub`, `jti`, `ver`, `iat`, `exp`; sin PII) |
| Validación / errores | Jakarta Bean Validation, `@RestControllerAdvice` con envelope + `X-Request-Id` |
| Observabilidad | Spring Boot Actuator (`/actuator/health`) |
| Pruebas | JUnit 5 — `docker compose exec nexusmarket-app mvn test` |

## Estructura del repositorio

```text
.
├── nexusmarket/           # Aplicación Spring Boot (código + tests + resources)
│   ├── Dockerfile
│   └── src/main/java/application/
│       ├── domain/        # Núcleo puro: models, valueobjects, enums, exceptions,
│       │                  #   ports/in (5 roles + acceso público), ports/out, services
│       ├── adapters/      # rest, useCases, persistence (jpa, mongodb),
│       │                  #   security, config, notification
│       └── infrastructure/ # seed (DatabaseSeeder)
├── SDD/                   # Especificación: documentos numerados, Software Architecture,
│                          #   domain, Adapters y C4
├── docker-compose.yml     # App + MySQL 3306 + MongoDB 27017
├── SETUP.md               # Guía paso a paso
└── LICENSE
```

## Seguridad y roles

- Autenticación JWT sin sesión. El token solo lleva identidad (`sub`, `jti`, `ver` = `User.authTokenVersion`); el rol y el estado se resuelven siempre del modelo persistido.
- Cambiar contraseña, estado o rol, o hacer logout, incrementa la versión e **invalida los tokens anteriores** (403).
- Roles: `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`, con un puerto de entrada por rol y acceso público (`/api/v1/auth`). Ya no existe el header `X-Actor-Id`.
- Semántica HTTP: 200/201/204 éxito, 400 validación, 401 credenciales inválidas, 403 sin permiso, 404 no encontrado, 409 regla de negocio.
- CORS restringido por `FRONTEND_ORIGIN` (por defecto `http://localhost:5173`).

## Variables de entorno

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `MONGODB_URI`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `FRONTEND_ORIGIN`, `SERVER_PORT`, `INVOICE_DEFAULT_TAX_RATE`, `INVENTORY_LOW_STOCK_THRESHOLD`. Los valores por defecto sirven para desarrollo; en cualquier otro entorno cambia `JWT_SECRET`.

## Documentación

Ver [`SDD/README.md`](SDD/README.md).

## Licencia

MIT — ver [`LICENSE`](LICENSE).
