# REST Security and CORS

- Spring Security **stateless**; `JwtAuthenticationFilter` valida el token (`sub`, `jti`, `ver`) mediante `JwtServicePort`, carga el usuario persistido y compara `ver` con `User.authTokenVersion`: si no coincide, el token está invalidado (403).
- BCrypt vía `PasswordServicePort`.
- Rutas públicas: `/api/v1/auth/**` y `/actuator/health`; el resto requiere autenticación y el dominio valida el rol.
- CORS restringido por `FRONTEND_ORIGIN` (por defecto `http://localhost:5173`).
- Cambiar contraseña, estado o rol, o hacer logout, incrementa `authTokenVersion` e invalida los tokens anteriores.
