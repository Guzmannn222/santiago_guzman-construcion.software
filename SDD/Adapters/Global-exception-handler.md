# Global Exception Handler

`@RestControllerAdvice` que traduce excepciones a un envelope JSON uniforme (`timestamp`, `status`, `error`, `message`, `path`, `details`) y devuelve el encabezado `X-Request-Id`.

| Excepción | HTTP |
|---|---|
| `MethodArgumentNotValidException`, `DomainValidationException` | 400 |
| `InvalidCredentialsException` | 401 |
| `UnauthorizedOperationException`, token ausente u obsoleto | 403 |
| `EntityNotFoundException` | 404 |
| `Duplicate*Exception`, `DomainException` (reglas de negocio / transiciones inválidas) | 409 |
| Otras | 500 (mensaje genérico, sin filtrar detalles internos) |
