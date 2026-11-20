# REST Validation

Doble capa de validación:

1. **Adaptador (Bean Validation)** en los DTO de entrada: `@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@Positive`, `@DecimalMin`, etc. Falla con HTTP 400 y detalle por campo.
2. **Dominio**: cada modelo y servicio valida sus invariantes (`DomainValidationException` y subclases `Invalid*Exception`, HTTP 400) y sus reglas de negocio (`DomainException`, HTTP 409).

La validación del adaptador no sustituye la del dominio.
