# Práctica 4: Trabajo en equipo con GitFlow y despliegue en producción

## Equipo XX - Metodologías Ágiles 2025-A EPN

### Miembros del equipo:
- Bryan David Salazar Morocho
- [Nombre del segundo miembro]
- [Nombre del tercer miembro]

## Documentación técnica de los cambios introducidos

### 1. Configuración de Docker mejorada

Se ha modificado el `Dockerfile` para permitir el paso de parámetros de configuración:

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["sh","-c","java -Djava.security.egd=file:/dev/urandom -jar /app.jar ${0} ${@}"]
```

Esto permite ejecutar el contenedor con parámetros adicionales:
```bash
docker run --rm <usuario>/mads-todolist-equipoXX --spring.profiles.active=postgres --POSTGRES_HOST=host-prueba
```

### 2. Perfiles de configuración PostgreSQL

#### Perfil de desarrollo (application-postgres.properties):
- Configuración para desarrollo con PostgreSQL
- `spring.jpa.hibernate.ddl-auto=update` para actualización automática del esquema
- Variables de entorno configurables para host, puerto, usuario y contraseña

#### Perfil de producción (application-postgres-prod.properties):
- Configuración para producción con PostgreSQL
- `spring.jpa.hibernate.ddl-auto=validate` para validación del esquema sin modificaciones
- Mismas variables de entorno configurables

### 3. Dependencias añadidas

Se ha añadido la dependencia de PostgreSQL al `pom.xml`:
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 4. Actualización de versiones

- **Versión del proyecto**: 1.3.0-SNAPSHOT
- **Nombre del proyecto**: todolist-equipo-XX
- **Página "Acerca de"**: Actualizada con información del equipo

## Detalles del despliegue de producción

### Configuración de red Docker

```bash
# Crear red para conectar aplicación y base de datos
docker network create network-equipo

# Lanzar contenedor PostgreSQL
docker run -d --network network-equipo --network-alias postgres \
  -v ${PWD}:/mi-host --name db-equipo \
  -e POSTGRES_USER=mads -e POSTGRES_PASSWORD=mads -e POSTGRES_DB=mads \
  postgres:13

# Lanzar aplicación conectada a la base de datos
docker run --rm --network network-equipo -p8080:8080 \
  <usuario>/mads-todolist-equipoXX:1.3.0-snapshot \
  --spring.profiles.active=postgres-prod --POSTGRES_HOST=postgres
```

### Gestión de base de datos de producción

#### Copias de seguridad
```bash
# Crear copia de seguridad
docker exec -it db-equipo bash
pg_dump -U mads --clean mads > /mi-host/backup-$(date +%Y%m%d).sql

# Restaurar copia de seguridad
docker exec -it db-equipo bash
psql -U mads mads < /mi-host/backup-YYYYMMDD.sql
```

#### Migraciones de base de datos
```bash
# Generar esquema actual
docker exec -it db-equipo bash
pg_dump -U mads -s mads > /mi-host/schema-1.3.0.sql

# Aplicar migración
docker exec -it db-equipo bash
psql -U mads mads < /mi-host/schema-1.2.0-1.3.0.sql
```

## Esquemas de datos

### Versión 1.2.0
[El esquema se generará al ejecutar la aplicación con el perfil postgres]

### Versión 1.3.0
[El esquema se generará al ejecutar la aplicación con el perfil postgres]

### Script de migración
[Se creará comparando los esquemas de las versiones 1.2.0 y 1.3.0]

## URL de la imagen Docker

La imagen Docker estará disponible en:
```
<usuario-docker>/mads-todolist-equipoXX:1.3.0-snapshot
```

## Flujo de trabajo GitFlow implementado

### Ramas principales:
- `main`: Código en producción
- `develop`: Rama de desarrollo principal

### Ramas de feature:
- Salen de `develop`
- Se integran en `develop` mediante Pull Requests
- Requieren revisión de código obligatoria

### Ramas de release:
- Salen de `develop`
- Se integran en `main` y `develop`
- Incluyen cambios específicos de versión (números de versión, esquemas de BD)

## Comandos Git para trabajo en equipo

### Trabajar en ramas compartidas:
```bash
# Crear y subir rama (responsable del issue)
git checkout -b nueva-rama
git push -u origin nueva-rama

# Descargar rama (otros miembros)
git pull
git checkout nueva-rama

# Subir cambios
git add .
git commit -m "Mis cambios"
git push

# Resolver conflictos
git pull
# Editar archivos en conflicto
git add .
git commit -m "Arreglado conflicto"
git push
```

### Configuración de revisión de código:
- Requerir 1 revisor mínimo en Pull Requests
- Configurar protección de rama `main` y `develop`
- Obligar revisión antes de merge

## Próximos pasos

1. [ ] Configurar equipo en GitHub
2. [ ] Crear tablero de proyecto
3. [ ] Implementar GitFlow completo
4. [ ] Desarrollar features en equipo
5. [ ] Crear release 1.3.0
6. [ ] Desplegar en producción
7. [ ] Documentar migraciones de base de datos 