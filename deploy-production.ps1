# Script de despliegue en producción para Windows
# Despliega la aplicación ToDoList con perfil postgres-prod

Write-Host '=== DESPLIEGUE EN PRODUCCIÓN ===' -ForegroundColor Green
Write-Host 'Iniciando despliegue de la aplicación en modo producción...' -ForegroundColor Yellow

# Obtener el directorio actual
$currentDir = Get-Location

# 1. Crear la red de Docker si no existe
Write-Host '1. Creando red de Docker...' -ForegroundColor Cyan
docker network create network-equipo 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host '   Red "network-equipo" creada exitosamente' -ForegroundColor Green
} else {
    Write-Host '   Red "network-equipo" ya existe' -ForegroundColor Yellow
}

# 2. Detener y eliminar contenedor de base de datos si existe
Write-Host '2. Preparando contenedor de base de datos...' -ForegroundColor Cyan
docker stop db-equipo 2>$null
docker rm db-equipo 2>$null

# 3. Lanzar contenedor de PostgreSQL
Write-Host '3. Lanzando contenedor PostgreSQL...' -ForegroundColor Cyan
docker run -d --network network-equipo --network-alias postgres `
    -v "${currentDir}:/mi-host" --name db-equipo `
    -e POSTGRES_USER=mads -e POSTGRES_PASSWORD=mads -e POSTGRES_DB=mads `
    postgres:13

if ($LASTEXITCODE -eq 0) {
    Write-Host '   Contenedor PostgreSQL lanzado exitosamente' -ForegroundColor Green
} else {
    Write-Host '   ERROR: No se pudo lanzar el contenedor PostgreSQL' -ForegroundColor Red
    exit 1
}

# 4. Esperar a que PostgreSQL esté listo
Write-Host '4. Esperando a que PostgreSQL esté listo...' -ForegroundColor Cyan
Start-Sleep -Seconds 10

# 4.1. Cargar el esquema de la base de datos si no existe la tabla principal
Write-Host '4.1. Cargando esquema de la base de datos...' -ForegroundColor Cyan
docker exec -i db-equipo psql -U mads -d mads -c "\dt" | Select-String "No relations found" > $null
if ($LASTEXITCODE -eq 0) {
    docker exec -i db-equipo psql -U mads -d mads -f /mi-host/sql/schema-1.3.0.sql
    Write-Host '   Esquema cargado correctamente.' -ForegroundColor Green
} else {
    Write-Host '   La base de datos ya tiene tablas, no se cargó el esquema.' -ForegroundColor Yellow
}

# 5. Verificar que el contenedor esté corriendo
Write-Host '5. Verificando estado del contenedor...' -ForegroundColor Cyan
$containerStatus = docker ps --filter "name=db-equipo" --format "table {{.Names}}\t{{.Status}}"
Write-Host $containerStatus -ForegroundColor White

# 6. Desplegar la aplicación en modo producción
Write-Host '6. Desplegando aplicación en modo producción...' -ForegroundColor Cyan
Write-Host '   Usando perfil: postgres-prod' -ForegroundColor Yellow
Write-Host '   Puerto: 8080' -ForegroundColor Yellow
Write-Host '   Presiona Ctrl+C para detener la aplicación' -ForegroundColor Yellow

docker run --rm --network network-equipo -p8080:8080 `
    bryanhert/mads-todolist-equipo2:1.3.0-snapshot `
    --spring.profiles.active=postgres-prod --POSTGRES_HOST=postgres

Write-Host '=== DESPLIEGUE COMPLETADO ===' -ForegroundColor Green
Write-Host 'La aplicación está disponible en: http://localhost:8080' -ForegroundColor Green