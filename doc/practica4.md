# Práctica 4: Trabajo en equipo con GitFlow y despliegue en producción

## Equipo 2 - ToDoList Spring Boot

### Miembros del equipo:
- Bryan  Salazar 
- Francisco Tamayo
- Maicol Nacimba
- Dylan Granizo
- Anderson Cango
---

## 1. Breve documentación técnica de los cambios introducidos

### 1.1 Cambios principales en la versión 1.3.0
- Se añadió el campo `fecha_registro` a la tabla `usuarios` para auditoría.
- Se creó un índice sobre el campo `email` en la tabla `usuarios` para mejorar el rendimiento de búsqueda.
- Scripts de migración y esquemas de datos actualizados.
- Scripts de despliegue multiplataforma (Linux/Mac y Windows).

### 1.2 Priorización de tareas

A partir de la versión 1.3.0, los usuarios pueden asignar una prioridad a cada tarea para organizar mejor su trabajo según la importancia:
- **Alta**: Tareas urgentes o críticas.
- **Media**: Tareas importantes pero no urgentes (valor por defecto).
- **Baja**: Tareas de menor importancia.

**Creación y edición:**
- Al crear o editar una tarea, el usuario puede seleccionar la prioridad desde un menú desplegable en el formulario.
- Si no se selecciona ninguna prioridad, se asigna "Media" por defecto.

**Visualización:**
- En la lista de tareas, la prioridad se muestra con una etiqueta de color:
  - Alta: rojo
  - Media: amarillo
  - Baja: verde

**Implementación técnica:**
- Se utiliza la enumeración `PrioridadTarea` en el backend (`ALTA`, `MEDIA`, `BAJA`).
- El modelo, DTO y formularios están adaptados para soportar la prioridad.
- Los controladores pasan la lista de prioridades a las vistas para que el usuario pueda elegir.

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
  bryanhert/mads-todolist-equipo2:1.3.0 \
  --spring.profiles.active=postgres-prod \
  --POSTGRES_HOST=postgres
```

### 2.3 Scripts de automatización
- `deploy-production.sh` (Linux/Mac)
- `deploy-production.ps1` (Windows PowerShell)

---

## 3. Esquemas de datos de las versiones 1.2.0 y 1.3.0

### 3.1 Esquema versión 1.2.0
Archivo: `sql/schema-1.2.0.sql`
```sql
-- Esquema de datos versión 1.2.0
-- Generado automáticamente por Hibernate

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    fecha_nacimiento date,
    admin boolean DEFAULT false NOT NULL,
    bloqueado boolean DEFAULT false NOT NULL,
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

-- Secuencias para los IDs
CREATE SEQUENCE public.usuarios_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE public.tareas_id_seq START WITH 1 INCREMENT BY 1;

-- Datos iniciales de prueba
INSERT INTO public.usuarios (id, email, nombre, password, admin, bloqueado) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false, false); 
```

### 3.2 Esquema versión 1.3.0
Archivo: `sql/schema-1.3.0.sql`
```sql
-- Esquema de datos versión 1.3.0
-- Generado automáticamente por Hibernate

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    fecha_nacimiento date,
    admin boolean DEFAULT false NOT NULL,
    bloqueado boolean DEFAULT false NOT NULL,
    fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP,
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

-- Secuencias para los IDs
CREATE SEQUENCE public.usuarios_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE public.tareas_id_seq START WITH 1 INCREMENT BY 1;

-- Índices para mejorar el rendimiento
CREATE INDEX idx_usuarios_email ON public.usuarios(email);

-- Datos iniciales de prueba
INSERT INTO public.usuarios (id, email, nombre, password, admin, bloqueado, fecha_registro) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false, false, CURRENT_TIMESTAMP); 
```

---

## 4. Script de migración de la base de datos
Archivo: `sql/schema-1.2.0-1.3.0.sql`
```sql
-- Script de migración de la versión 1.2.0 a la 1.3.0
-- Este script actualiza la base de datos de producción de la versión 1.2.0 a la 1.3.0

-- Añadir campo fecha_registro a la tabla usuarios
ALTER TABLE public.usuarios ADD COLUMN fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP;

-- Crear índice para mejorar el rendimiento de búsquedas por email
CREATE INDEX idx_usuarios_email ON public.usuarios(email);

-- Comentario: Esta migración añade un campo de auditoría a la tabla usuarios
-- El campo se añade con valor por defecto para mantener la compatibilidad 
```

---

## 5. URL de la imagen Docker de la aplicación

La imagen Docker de la aplicación está disponible en:

https://hub.docker.com/repository/docker/bryanhert/mads-todolist-equipo2/tags

---

## 6. Gestión de copias de seguridad

### 6.1 Crear copia de seguridad
```bash
docker exec -it db-equipo bash
pg_dump -U mads --clean mads > /mi-host/backup-$(date +%Y%m%d).sql
exit
```

### 6.2 Restaurar copia de seguridad
```bash
docker exec -it db-equipo bash
psql -U mads mads < /mi-host/backup-YYYYMMDD.sql
exit
```

### 6.3 Copias de seguridad disponibles
- `sql/backup-20250704.sql` - Copia de seguridad del 4 de julio de 2025

---

## 7. Implementación de GitFlow

### 7.1 Estructura de ramas
- **main**: Rama principal para releases
- **develop**: Rama de desarrollo
- **release-1.3.0**: Rama de release para la versión 1.3.0
- **feature/***: Ramas de características

### 7.2 Flujo de trabajo
1. **Desarrollo**: Las nuevas características se desarrollan en ramas `feature/*` desde `develop`
2. **Release**: Cuando `develop` está lista para un release, se crea una rama `release-X.Y.Z`
3. **Producción**: La rama de release se integra en `main` y se etiqueta con la versión
4. **Hotfix**: Los hotfixes se crean desde `main` y se integran en `main` y `develop`

### 7.3 Comandos GitFlow
```bash
# Crear rama de release
git checkout develop
git checkout -b release-1.3.0
```

---

## 8. Pruebas de funcionamiento

### 8.1 Prueba del perfil de producción

Para verificar que el perfil de producción funciona correctamente:

1. Lanzar base de datos vacía
2. Intentar ejecutar aplicación con perfil `postgres-prod`
3. Verificar que falla (esquema no existe)
4. Restaurar esquema de datos
5. Verificar que funciona correctamente

### 8.2 Prueba de migración

1. Restaurar copia de seguridad de versión anterior
2. Aplicar script de migración
3. Verificar que la aplicación funciona con los datos existentes

---

## 9. Conclusiones

La implementación de GitFlow y el despliegue en producción ha permitido:

- **Control de versiones**: Gestión estructurada de releases
- **Seguridad en producción**: Validación de esquema sin modificaciones automáticas
- **Automatización**: Scripts para facilitar el despliegue
- **Backup y recuperación**: Procedimientos para proteger los datos
- **Escalabilidad**: Arquitectura preparada para futuras versiones

