# SDD — Especificación, Análisis y Diseño de NexusMarket

Esta carpeta contiene el **Documento de Análisis y Diseño (SDD)** del sistema NexusMarket,
dividido en archivos Markdown por bloque temático para facilitar la lectura, el versionamiento
y la trazabilidad en el repositorio. El documento original en Word se conserva en
[`NexusMarket_Analisis_y_Diseno.docx`](./NexusMarket_Analisis_y_Diseno.docx) como fuente de verdad.

## Estructura

| Archivo | Contenido | Secciones del documento original |
|---|---|---|
| [`00-portada-y-control-cambios.md`](./00-portada-y-control-cambios.md) | Portada, ficha técnica y control de versiones | — |
| [`01-introduccion-y-contexto.md`](./01-introduccion-y-contexto.md) | Introducción, objetivos y alcance | 1 – 3 |
| [`02-descripcion-del-negocio.md`](./02-descripcion-del-negocio.md) | Descripción general, participantes y modelo operativo | 4 – 6 |
| [`03-dominios-funcionales.md`](./03-dominios-funcionales.md) | Dominios funcionales D1–D11 | 7 – 15 |
| [`04-restricciones-y-validaciones.md`](./04-restricciones-y-validaciones.md) | Restricciones generales y validaciones críticas | 16 |
| [`05-casos-de-uso.md`](./05-casos-de-uso.md) | Catálogo y especificación de casos de uso | 17 – 19 |
| [`06-diagrama-actividad.md`](./06-diagrama-actividad.md) | Diagrama de actividad — proceso de compra | 20 |
| [`07-modelo-de-clases.md`](./07-modelo-de-clases.md) | Clases del dominio, atributos, métodos y enumeraciones | 21 – 23 |
| [`08-diagrama-clases-y-relaciones.md`](./08-diagrama-clases-y-relaciones.md) | Diagrama UML de clases y multiplicidades | 24 – 25 |
| [`09-diagrama-secuencia.md`](./09-diagrama-secuencia.md) | Diagrama de secuencia — creación de pedido | 26 |
| [`10-trazabilidad.md`](./10-trazabilidad.md) | Matriz de trazabilidad objetivo–dominio–caso de uso–clase | 27 |
| [`11-arquitectura-y-repositorio.md`](./11-arquitectura-y-repositorio.md) | Arquitectura en capas y estructura del repositorio | 28 – 29 |
| [`12-implementacion.md`](./12-implementacion.md) | Matriz de responsabilidades y consideraciones de implementación | 30 – 31 |
| [`13-glosario-y-conclusiones.md`](./13-glosario-y-conclusiones.md) | Glosario, conclusiones y referencias | 32 – 33 |
| [`diagramas/`](./diagramas/) | Imágenes de los diagramas UML extraídas del documento original |  |

## Cómo usar esta carpeta

- El código Java del proyecto (paquete `com.nexusmarket`) vive en [`../nexusmarket`](../nexusmarket), **no** aquí.
- Cualquier cambio de diseño (nuevas clases, cambios de reglas de negocio, nuevos casos de uso) debe
  reflejarse primero en el archivo Markdown correspondiente y luego implementarse en el código.
- Los diagramas se mantienen como imágenes; si se regeneran, deben reemplazarse en `diagramas/`
  conservando el mismo nombre de archivo para no romper los enlaces de este índice.
