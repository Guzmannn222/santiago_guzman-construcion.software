# Persistence Adapters

## MySQL (JPA)

`adapters/persistence/jpa`: entidades JPA (`*JpaEntity`), repositorios Spring Data, mappers entidad↔dominio y adaptadores que implementan los `*RepositoryPort`. Esquema generado por Hibernate (`ddl-auto=update`) en `nexusmarket_db`. `Money` se persiste como `amount` + `currency`.

## MongoDB (auditoría)

`adapters/persistence/mongodb`: documento `AuditLogDocument` en la colección `audit_logs` de `audit_db`, con adaptador de `AuditLogRepositoryPort`. Es append-only: no hay actualización ni borrado.

Las operaciones de negocio (`Operation`) se guardan en MySQL; el detalle de auditoría (`AuditLog`) en MongoDB.
