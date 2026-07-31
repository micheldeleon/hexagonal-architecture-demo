# Base de datos

El esquema activo se administra exclusivamente con Flyway desde:

```text
src/main/resources/db/migration/
```

`legacy-sql/` conserva scripts históricos anteriores a Flyway sólo como
referencia. No deben ejecutarse manualmente ni copiarse a producción. Sus
cambios quedaron consolidados en la migración `V1`.
