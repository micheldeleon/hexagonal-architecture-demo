# syntax=docker/dockerfile:1
# Indica la version sintaxis del Dockerfile

# Build stage
# Usa una imagen base con Maven + JDK 21 para compilar. Se llama etapa build.
#fija app como carpeta del trabajo
#Ejecuta Maven en modo quiet, sin tests, para generar el jar en target/.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app 
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
#Define variable PORT por defecto 8080 y documenta que el contenedor escucha ese puerto.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
