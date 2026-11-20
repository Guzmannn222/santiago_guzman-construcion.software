# 1–3. Introducción, Objetivos y Alcance

## 1. Introducción y Contexto

NexusMarket es una plataforma digital centralizada que actúa como intermediario comercial entre
compradores y vendedores. El sistema administra de forma integral la operación del negocio, desde
el registro de usuarios y la publicación de productos hasta la logística, la facturación y los
procesos de posventa, garantizando trazabilidad y coordinación entre todos los participantes.

El propósito de este documento es transformar la **Especificación Funcional del Negocio —
NexusMarket** en un modelo de software orientado a objetos que pueda implementarse posteriormente
en Java. Para ello se conserva una separación explícita entre lo definido por el negocio
(objetivos, alcance, dominios y reglas) y las decisiones de diseño necesarias para programarlo
(clases, arquitectura y diagramas UML).

El documento está organizado en cuatro grandes bloques: **(i)** el contexto y alcance del negocio;
**(ii)** los dominios funcionales y sus reglas; **(iii)** el modelo de análisis y diseño orientado
a objetos (casos de uso, clases, actividad, secuencia y trazabilidad); y **(iv)** la propuesta de
arquitectura e implementación en Java.

## 2. Objetivos del Sistema

El sistema deberá permitir el cumplimiento de los siguientes doce objetivos estratégicos,
establecidos por la especificación funcional del negocio:

| Código | Objetivo funcional |
|---|---|
| OBJ-01 | Administrar la información de todos los usuarios del Marketplace. |
| OBJ-02 | Gestionar el registro y administración de vendedores. |
| OBJ-03 | Administrar compradores registrados. |
| OBJ-04 | Controlar la información de las bodegas. |
| OBJ-05 | Gestionar el catálogo de productos. |
| OBJ-06 | Administrar el inventario distribuido. |
| OBJ-07 | Gestionar el carrito de compras. |
| OBJ-08 | Controlar el ciclo completo de los pedidos. |
| OBJ-09 | Administrar la facturación de las compras. |
| OBJ-10 | Gestionar los procesos logísticos. |
| OBJ-11 | Administrar devoluciones y reembolsos. |
| OBJ-12 | Consolidar información administrativa para consulta. |

*Tabla 1. Objetivos funcionales del sistema NexusMarket.*

## 3. Alcance del Sistema

El sistema administra exclusivamente los procesos descritos en la especificación funcional del
negocio.

### 3.1 Procesos incluidos

Registro de compradores · Administración de usuarios · Registro administrativo de vendedores ·
Administración de bodegas · Gestión del catálogo de productos · Administración del inventario ·
Gestión del carrito de compras · Gestión de pedidos · Gestión de facturación · Gestión de envíos ·
Gestión de devoluciones · Gestión de reembolsos · Consulta de reportes administrativos.

*Tabla 2. Procesos incluidos en el alcance funcional del sistema.*

### 3.2 Procesos fuera del alcance

La especificación funcional indica explícitamente que quedan fuera del alcance del negocio los
siguientes aspectos:

- Interfaces gráficas de usuario.
- Aplicaciones móviles y portales web.
- Mecanismos técnicos de autenticación.
- Tecnologías específicas de implementación.
- Arquitectura del software.
- Detalles de almacenamiento de la información.

Por lo tanto, las secciones de este documento relacionadas con Java, la arquitectura en capas y
los diagramas UML **son decisiones de diseño académico**, y no requisitos adicionales impuestos
por el negocio.
