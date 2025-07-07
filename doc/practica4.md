# Práctica 4: Trabajo en equipo con GitFlow y despliegue en producción

## Equipo XX - ToDoList Spring Boot

### Miembros del equipo:
- Bryan David Salazar Morocho
- [Nombre del segundo miembro]
- [Nombre del tercer miembro]

---

## 1. Documentación técnica de los cambios introducidos

### 1.1 Configuración Docker mejorada

Se ha actualizado el `Dockerfile` para permitir el paso de parámetros de configuración:

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/*.jar app.jar

ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/urandom -jar /app.jar ${0} ${@}"]
```

Esta configuración permite ejecutar el contenedor con parámetros adicionales:
```bash
docker run --rm usuario/mads-todolist:1.3.0 --spring.profiles.active=postgres --POSTGRES_HOST=host-prueba
```

### 1.2 Configuración de PostgreSQL con variables de entorno

El archivo `application-postgres.properties` utiliza variables de entorno para la configuración:

```properties
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
DB_USER=mads
DB_PASSWD=mads

spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST}:${POSTGRES_PORT}/mads
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWD}
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQL9Dialect
spring.jpa.hibernate.ddl-auto=update
spring.sql.init.mode=never
```

### 1.3 Perfil de producción

Se ha creado el archivo `application-postgres-prod.properties` con configuración específica para producción:

```properties
# Misma configuración que postgres pero con:
spring.jpa.hibernate.ddl-auto=validate
```

El modo `validate` asegura que la aplicación no modifique automáticamente el esquema de la base de datos en producción.

---

## 2. Detalles del despliegue de producción

### 2.1 Arquitectura de despliegue

La aplicación se despliega usando dos contenedores Docker conectados por una red:

1. **Contenedor de base de datos**: PostgreSQL 13
2. **Contenedor de aplicación**: Spring Boot con perfil postgres-prod

### 2.2 Comandos de despliegue

#### Crear red de Docker:
```bash
docker network create network-equipo
```

#### Lanzar base de datos:
```bash
docker run -d \
  --network network-equipo \
  --network-alias postgres \
  -v ${PWD}:/mi-host \
  --name db-equipo \
  -e POSTGRES_USER=mads \
  -e POSTGRES_PASSWORD=mads \
  -e POSTGRES_DB=mads \
  postgres:13
```

#### Lanzar aplicación:
```bash
docker run --rm \
  --network network-equipo \
  -p 8080:8080 \
  bryanhert/mads-todolist:1.3.0 \
  --spring.profiles.active=postgres-prod \
  --POSTGRES_HOST=postgres
```

### 2.3 Scripts de automatización

Se han creado scripts para automatizar el despliegue:
- `deploy-production.sh` (Linux/Mac)
- `deploy-production.ps1` (Windows PowerShell)

---

## 3. Esquemas de datos

### 3.1 Esquema versión 1.2.0

Archivo: `sql/schema-1.2.0.sql`

```sql
CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    bloqueado boolean DEFAULT false,
    PRIMARY KEY (id)
);

CREATE TABLE public.tareas (
    id bigint NOT NULL,
    titulo character varying(255),
    descripcion text,
    fecha_limite timestamp,
    completada boolean DEFAULT false,
    usuario_id bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id)
);
```

### 3.2 Esquema versión 1.3.0

Archivo: `sql/schema-1.3.0.sql`

El esquema de la versión 1.3.0 es idéntico al de la 1.2.0, ya que no se han introducido cambios en el modelo de datos.

### 3.3 Script de migración

Archivo: `sql/schema-1.2.0-1.3.0.sql`

```sql
-- Script de migración de la versión 1.2.0 a la 1.3.0
-- No hay cambios en el esquema de datos entre estas versiones
-- Este script se mantiene como plantilla para futuras migraciones
```

---

## 4. Gestión de copias de seguridad

### 4.1 Crear copia de seguridad

```bash
docker exec -it db-equipo bash
pg_dump -U mads --clean mads > /mi-host/backup-$(date +%Y%m%d).sql
exit
```

### 4.2 Restaurar copia de seguridad

```bash
docker exec -it db-equipo bash
psql -U mads mads < /mi-host/backup-YYYYMMDD.sql
exit
```

### 4.3 Copias de seguridad disponibles

- `sql/backup-20250704.sql` - Copia de seguridad del 4 de julio de 2025

---

## 5. Implementación de GitFlow

### 5.1 Estructura de ramas

- **main**: Rama principal para releases
- **develop**: Rama de desarrollo
- **release-1.3.0**: Rama de release para la versión 1.3.0
- **feature/***: Ramas de características

### 5.2 Flujo de trabajo

1. **Desarrollo**: Las nuevas características se desarrollan en ramas `feature/*` desde `develop`
2. **Release**: Cuando `develop` está lista para un release, se crea una rama `release-X.Y.Z`
3. **Producción**: La rama de release se integra en `main` y se etiqueta con la versión
4. **Hotfix**: Los hotfixes se crean desde `main` y se integran en `main` y `develop`

### 5.3 Comandos GitFlow

```bash
# Crear rama de release
git checkout develop
git checkout -b release-1.3.0

# Integrar release en main
git checkout main
git merge release-1.3.0
git tag -a v1.3.0 -m "Release version 1.3.0"

# Integrar release en develop
git checkout develop
git merge release-1.3.0

# Eliminar rama de release
git branch -d release-1.3.0
```

---

## 6. URL de la imagen Docker

**Imagen Docker Hub**: `bryanhert/mads-todolist:1.3.0`

**Comando para descargar**:
```bash
docker pull bryanhert/mads-todolist:1.3.0
```

**Comando para construir localmente**:
```bash
./mvnw package
docker build -t bryanhert/mads-todolist:1.3.0 .
```

---

## 7. Pruebas de funcionamiento

### 7.1 Prueba del perfil de producción

Para verificar que el perfil de producción funciona correctamente:

1. Lanzar base de datos vacía
2. Intentar ejecutar aplicación con perfil `postgres-prod`
3. Verificar que falla (esquema no existe)
4. Restaurar esquema de datos
5. Verificar que funciona correctamente

### 7.2 Prueba de migración

1. Restaurar copia de seguridad de versión anterior
2. Aplicar script de migración
3. Verificar que la aplicación funciona con los datos existentes

---

## 8. Conclusiones

La implementación de GitFlow y el despliegue en producción ha permitido:

- **Control de versiones**: Gestión estructurada de releases
- **Seguridad en producción**: Validación de esquema sin modificaciones automáticas
- **Automatización**: Scripts para facilitar el despliegue
- **Backup y recuperación**: Procedimientos para proteger los datos
- **Escalabilidad**: Arquitectura preparada para futuras versiones

La aplicación está lista para ser desplegada en un entorno de producción real con todas las garantías de seguridad y mantenimiento necesarias. 