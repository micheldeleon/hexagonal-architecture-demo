# Gestión de Torneos UY — Backend

Backend REST para gestionar usuarios, organizadores y torneos con formatos
eliminatorios, liga y carreras. Incluye inscripciones individuales y por
equipos, fixtures, resultados, reputación, notificaciones en tiempo real,
moderación administrativa y un sistema de publicaciones.

## Tecnologías

- Java 21
- Spring Boot 3.5
- Maven Wrapper
- PostgreSQL y Spring Data JPA
- Spring Security, JWT y Google Login
- Supabase Storage
- Server-Sent Events (SSE)
- Docker

## Arquitectura

El código sigue una arquitectura hexagonal:

```text
src/main/java/com/example/demo/
├── core/
│   ├── domain/       # Modelos y reglas de negocio
│   ├── application/  # Casos de uso y servicios
│   └── ports/        # Contratos de entrada y salida
├── adapters/
│   ├── in/api/       # Controladores, DTO, mappers y seguridad
│   └── out/          # Persistencia, correo y servicios externos
└── config/           # Composición de dependencias
```

## Requisitos

- JDK 21
- Una base PostgreSQL accesible
- Credenciales de Supabase Storage

No es necesario instalar Maven: el repositorio incluye Maven Wrapper.

En Ubuntu:

```bash
sudo apt update
sudo apt install openjdk-21-jdk
java -version
```

## Configuración local

Copiar el archivo de ejemplo:

```bash
cp .env.example .env
```

Completar `.env` con los valores del entorno. El archivo está ignorado por Git
y nunca debe subirse al repositorio.

Variables principales:

| Variable | Uso |
| --- | --- |
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base |
| `JWT_SECRET` | Secreto para firmar JWT; usar al menos 32 bytes |
| `SUPABASE_URL` | URL del proyecto Supabase |
| `SUPABASE_STORAGE_KEY` | Clave de acceso a Storage |
| `GOOGLE_CLIENT_ID` | Client ID de Google Login |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos, separados por coma |
| `MAILJET_ENABLED` | Habilita el envío real de correo |
| `MAILJET_APIKEY` | API key de Mailjet |
| `MAILJET_SECRETKEY` | Secret key de Mailjet |
| `CONTACT_TO_EMAIL` | Destino del formulario de contacto |

La clave de Mailjet debe llamarse `MAILJET_APIKEY`; la variante histórica
`MAILJET_APIKET` ya no es compatible.

## Ejecutar

```bash
./mvnw spring-boot:run
```

La API queda disponible por defecto en `http://localhost:8080`. Comprobaciones:

```bash
curl http://localhost:8080/
curl http://localhost:8080/actuator/health
```

## Tests y build

La suite usa una base H2 en memoria y no necesita `.env`, PostgreSQL ni
credenciales externas:

```bash
./mvnw test
./mvnw clean verify
```

El JAR resultante queda en `target/tutorneo-0.0.1-SNAPSHOT.jar`.

## Docker

Primero generar el JAR y luego construir la imagen:

```bash
./mvnw clean package
docker build -t gestion-torneos-api .
docker run --env-file .env -p 8080:8080 gestion-torneos-api
```

El despliegue automatizado en servidor con Docker, GHCR, Cloudflare Tunnel y
rollback está documentado en
[`docs/deployment/servidor.md`](docs/deployment/servidor.md).

## Base de datos

Flyway administra los cambios de esquema desde
`src/main/resources/db/migration`. Hibernate usa
`spring.jpa.hibernate.ddl-auto=validate`, por lo que valida el mapeo sin
modificar la base. Las bases existentes se adoptan en la versión 0 y reciben
las migraciones pendientes automáticamente.

Para todo cambio futuro de esquema, agregar una migración inmutable siguiendo
el formato `V<n>__descripcion.sql`. No editar migraciones ya aplicadas.

## Documentación adicional

Consultar el [índice de documentación](docs/README.md).
