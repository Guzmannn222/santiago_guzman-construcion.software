# NexusMarket

Repositorio del proyecto académico **NexusMarket — Sistema de Marketplace**, desarrollado para la
asignatura de Ingeniería de Software (tercer semestre). Contiene la especificación funcional, el
análisis y diseño orientado a objetos, y la implementación en Java derivada de ese diseño.

## Contenido del repositorio

| Carpeta | Descripción |
|---|---|
| [`SDD/`](./SDD) | Documento de Análisis y Diseño (Software Design Document), dividido por secciones en Markdown, más los diagramas UML y el .docx original. |
| [`code/`](./code) | Proyecto Java (Maven), en inglés, con el modelo de dominio, enumeraciones y la estructura en capas (`model`, `enums`, `repository`, `service`, `controller`, `util`). |

## Resumen del proyecto

NexusMarket es una plataforma digital que actúa como intermediario comercial entre compradores y
vendedores, administrando de forma integral usuarios, vendedores, bodegas, catálogo, inventario,
pedidos, facturación, envíos y posventa. El documento completo de análisis está en
[`SDD/README.md`](./SDD/README.md).

- **11 dominios funcionales** (D1–D11)
- **15 casos de uso** (CU-01–CU-15)
- **17 clases de dominio** modeladas en Java
- **10 enumeraciones** para catálogos cerrados de valores
- Arquitectura en capas: `model → repository → service → controller`

## Cómo compilar y ejecutar el proyecto Java

Requiere Java 17+ y Maven. El código fuente completo (clases, comentarios e identificadores)
está escrito en inglés dentro de la carpeta [`code/`](./code).

```bash
cd code
mvn compile      # compilar
mvn test         # ejecutar pruebas unitarias
mvn package      # generar el .jar
java -cp target/nexusmarket.jar com.nexusmarket.Main
```

## Convenciones de commits sugeridas

Para mantener trazabilidad con el SDD, se recomienda referenciar el dominio o caso de uso en el
mensaje de commit, por ejemplo:

```
feat(model): implementar reglas de Inventario según D6 / CU-07
docs(SDD): actualizar matriz de trazabilidad (sección 27)
```

## Licencia

Este proyecto se distribuye bajo la licencia [MIT](./LICENSE).
