# 32–33. Glosario, Conclusiones y Referencias

## 32. Glosario de Términos

| Término | Definición |
|---|---|
| Bodega | Espacio físico donde se administra el inventario de productos, propio del Marketplace o de un Vendedor. |
| Carrito | Selección provisional de productos realizada por un comprador antes de confirmar un pedido. |
| Dominio funcional | Agrupación coherente de procesos y clases relacionadas con un área de negocio específica. |
| Enumeración (enum) | Tipo de dato que restringe un atributo a un catálogo cerrado de valores predefinidos. |
| Existencias | Cantidad disponible de un producto en una bodega determinada. |
| Multiplicidad | Notación UML que indica cuántas instancias de una clase pueden asociarse con instancias de otra. |
| Pedido | Compromiso comercial formal generado a partir de un carrito confirmado. |
| Posventa | Conjunto de procesos posteriores a la entrega, como devoluciones y reembolsos. |
| Rol | Conjunto de responsabilidades y permisos asignado a un usuario dentro del sistema. |

*Tabla 22. Glosario de términos utilizados en el documento.*

## 33. Conclusiones

La especificación funcional de NexusMarket se transformó en un documento de análisis y diseño
orientado a objetos, conservando sus objetivos, alcance, participantes, dominios, reglas y flujo
comercial originales. A partir de esa base se construyó un modelo de clases, casos de uso,
actividad, secuencia, trazabilidad y estructura de proyecto Java.

Los diagramas y las clases presentados constituyen una propuesta de diseño derivada de la fuente
funcional. Esto permite que el documento sea suficientemente concreto para iniciar la
programación, sin presentar como requisitos originales aquellas decisiones que no estaban
definidas en el documento base de negocio.

La matriz de trazabilidad ([`10-trazabilidad.md`](./10-trazabilidad.md)) confirma que los doce
objetivos funcionales quedan completamente cubiertos por los once dominios, los quince casos de
uso y las diecisiete clases del modelo, lo que valida la completitud del análisis frente a la
especificación de negocio.

## Referencias

- Especificación Funcional del Negocio — NexusMarket. Documento fuente elaborado por el área de
  negocio.
- Object Management Group (OMG). Unified Modeling Language (UML) Specification.
- Bibliografía de referencia de Ingeniería de Software — Tercer semestre, curso de Análisis y
  Diseño Orientado a Objetos.
