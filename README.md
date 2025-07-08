# ToDoList Spring Boot - Equipo 2

Aplicación ToDoList de la asignatura Metodologías Ágiles usando Spring Boot .

## Equipo de desarrollo

- Bryan Salazar
- Francisco Tamayo
- Maicol Nacimba
- Dylan Granizo
- Anderson Cango

## Descripción funcional

Esta aplicación permite a los usuarios:

- Registrarse y autenticarse.
- Gestionar tareas personales (crear, editar, eliminar, listar).
- Visualizar una página "Acerca de" con información del equipo y la versión.
- Los administradores pueden ver el listado de usuarios, ver detalles y bloquear/habilitar usuarios.

## Estructura del proyecto

- `src/main/java/madstodolist/controller`: Controladores web (MVC).
- `src/main/java/madstodolist/service`: Lógica de negocio y servicios.
- `src/main/java/madstodolist/model`: Entidades JPA.
- `src/main/java/madstodolist/repository`: Repositorios de acceso a datos.
- `src/main/resources/templates`: Vistas Thymeleaf.
- `src/test/java/madstodolist`: Pruebas automáticas (controladores, servicios, repositorios).
- `sql/`: Esquemas de base de datos y scripts de migración.
- `doc/`: Documentación técnica y de la práctica.

## Imagen Docker

La imagen está disponible en Docker Hub:
https://hub.docker.com/repository/docker/bryanhert/mads-todolist-equipo2/tags

## Despliegue en producción

### Arquitectura

La aplicación se despliega usando dos contenedores Docker conectados por una red:

1. **Contenedor de base de datos**: PostgreSQL 13
2. **Contenedor de aplicación**: Spring Boot con perfil postgres-prod

### Comandos de despliegue

Crear red de Docker:

```bash
docker network create network-equipo
```

Lanzar base de datos:

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

Lanzar aplicación:

```bash
docker run --rm \
  --network network-equipo \
  -p 8080:8080 \
  bryanhert/mads-todolist-equipo2:1.3.0 \
  --spring.profiles.active=postgres-prod \
  --POSTGRES_HOST=postgres
```

### Scripts de automatización

- `deploy-production.ps1`: Despliegue en producción (Windows PowerShell)

- `deploy-development.ps1`: Despliegue en entorno de desarrollo (Windows PowerShell)

## Esquemas de datos y migraciones

- Esquema versión 1.2.0: `sql/schema-1.2.0.sql`
- Esquema versión 1.3.0: `sql/schema-1.3.0.sql`
- Script de migración: `sql/schema-1.2.0-1.3.0.sql`
- Copia de seguridad: `sql/backup-20250704.sql`

## Ejecución en desarrollo

Puedes ejecutar la aplicación usando el _goal_ `run` del _plugin_ Maven de Spring Boot:

```bash
./mvnw spring-boot:run
```

O generar un `jar` y ejecutarlo:

```bash
./mvnw package
java -jar target/epn-todolist-Bryan_Salazar-1.0.1-SNAPSHOT.jar
```

## Ejecución con Docker

```bash
docker run --rm -p 8080:8080 bryanhert/mads-todolist-equipo2:1.3.0
```

## Credenciales de prueba

- Usuario de ejemplo: `user@ua`
- Contraseña: `123`
- (Puedes crear nuevos usuarios desde la página de registro)

## Cómo correr los tests

```bash
./mvnw test
```

## Repositorio GitHub

Repositorio oficial del proyecto:
https://github.com/SalazarBryan13/TodoListSpringBoot

## Cómo contribuir

1. Haz un fork del repositorio.
2. Crea una rama para tu feature o corrección: `git checkout -b mi-feature`.
3. Realiza tus cambios y haz commit.
4. Haz push a tu rama: `git push origin mi-feature`.
5. Abre un Pull Request en GitHub.

## Documentación técnica

Consulta la documentación técnica y detalles de despliegue en `doc/practica4.md`.
