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

`MAILJET_APIKET` se conserva temporalmente en `.env.example` por compatibilidad
con una clave histórica mal escrita. La aplicación debe migrarse a
`MAILJET_APIKEY`.

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

## Base de datos

Actualmente Hibernate usa `spring.jpa.hibernate.ddl-auto=update` y los cambios
históricos de esquema están documentados como scripts SQL en la raíz. Antes de
usar una nueva base o desplegar a producción, revisar esos scripts y hacer un
respaldo.

Una mejora pendiente es consolidar el esquema en migraciones versionadas con
Flyway o Liquibase y cambiar Hibernate a `ddl-auto=validate`.

## Documentación adicional

- `API_ENDPOINTS_TOURNAMENTS_POSTMAN.md`
- `API_ENDPOINTS_CONTACT_POSTMAN.md`
- `NOTIFICATIONS_README.md`
- `NOTIFICACIONES_SSE_FRONTEND.md`
- `REPUTATION_SYSTEM_FRONTEND_GUIDE.md`
- `BLOG_SYSTEM_README.md`
- `src/test/README_TESTS.md`
