# Guía paso a paso: poner en marcha NexusMarket

Todos los comandos se ejecutan desde la raíz del repositorio.

## 0. Prerrequisitos

1. **Docker** en ejecución: `docker --version` y `docker compose version`. Java 17 y Maven corren dentro del contenedor.
2. Puertos libres: **3306** (MySQL), **27017** (MongoDB) y **8080** (app). Si el 8080 está ocupado: `HOST_SERVER_PORT=8081 docker compose up -d --build`.

## 1. Levantar la aplicación y las bases de datos

```bash
docker compose up -d --build
docker ps --format "{{.Names}}|{{.Status}}"
docker compose exec nexusmarket-app mvn -version
```

Crea MySQL (`nexusmarket_db`) y MongoDB (`audit_db`) y arranca la app en `http://localhost:8080`. Credenciales por defecto de la base: `nexusmarket_app` / `nexusmarket_app_password` (sobrescribibles con `MYSQL_PASSWORD`).

## 2. Compilar y correr las pruebas

```bash
docker compose exec nexusmarket-app mvn test
```

Resultado esperado: `BUILD SUCCESS`. La prueba `contextLoads` necesita MySQL y MongoDB arriba (paso 1).

## 3. Verificar la aplicación

```bash
docker compose logs -f nexusmarket-app
curl -s http://127.0.0.1:8080/actuator/health
# {"groups":["liveness","readiness"],"status":"UP"}
```

## 4. Probar la API

Inicia sesión en `POST /api/v1/auth/login` y usa el token devuelto como `Authorization: Bearer <token>`. El `DatabaseSeeder` crea los usuarios iniciales (ver su código en `infrastructure/seed`).

## 5. Variables de entorno

`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `MONGODB_URI`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `FRONTEND_ORIGIN`, `SERVER_PORT`.

## 6. Detener

```bash
docker compose down        # conserva datos
docker compose down -v     # borra volúmenes
```
