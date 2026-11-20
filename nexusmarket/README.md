# nexusmarket — aplicación Spring Boot

Módulo ejecutable de NexusMarket (Java 17, Spring Boot 4.1, Maven).

## Capas

- `application.domain`: modelos, value objects, excepciones, puertos y servicios de dominio (sin Spring/JPA).
- `application.adapters`: `rest`, `useCases`, `persistence` (JPA/MySQL y MongoDB), `security`, `config`, `notification`.
- `application.infrastructure.seed`: carga inicial de datos.

## Ejecutar

Con Docker (recomendado), desde la raíz del repositorio:

```bash
docker compose up -d --build
docker compose exec nexusmarket-app mvn test
```

Sin Docker (requiere MySQL en 3306 y MongoDB en 27017):

```bash
./mvnw spring-boot:run     # puerto 8081 por defecto (SERVER_PORT)
./mvnw test
```

La autenticación es JWT (`Authorization: Bearer <token>`); el antiguo header `X-Actor-Id` y el almacenamiento en memoria ya no existen. Variables de entorno: ver el README de la raíz.
