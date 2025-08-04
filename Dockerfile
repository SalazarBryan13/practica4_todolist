# Multi-stage build para optimizar tamaño de imagen
# Stage 1: Build con Maven
FROM maven:3.8.1-openjdk-8-slim AS build
WORKDIR /app

# Copiar archivos de configuración Maven primero (para cache de dependencias)
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea si no cambia pom.xml)
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Runtime con imagen más pequeña
FROM openjdk:8-jre-alpine

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S todolist && \
    adduser -u 1001 -S todolist -G todolist

# Instalar herramientas necesarias
RUN apk --no-cache add curl dumb-init

# Configurar directorio de trabajo
WORKDIR /app

# Copiar JAR desde stage de build
COPY --from=build /app/target/*.jar app.jar

# Cambiar ownership de archivos al usuario todolist
RUN chown -R todolist:todolist /app

# Configurar variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=postgres-prod
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/urandom"
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Cambiar a usuario no-root
USER todolist

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/login || exit 1

# Usar dumb-init para mejor manejo de señales
ENTRYPOINT ["dumb-init", "--"]

# Comando de ejecución con configuración optimizada
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]