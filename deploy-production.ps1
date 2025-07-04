# Script de despliegue en producción para ToDoList (Windows PowerShell)
# Versión: 1.3.0

Write-Host "=== Despliegue en Producción ToDoList v1.3.0 ===" -ForegroundColor Green

# 1. Crear red de Docker
Write-Host "1. Creando red de Docker..." -ForegroundColor Yellow
docker network create network-equipo 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "Red ya existe" -ForegroundColor Gray
}

# 2. Detener y eliminar contenedores existentes
Write-Host "2. Deteniendo contenedores existentes..." -ForegroundColor Yellow
docker stop db-equipo 2>$null
docker rm db-equipo 2>$null

# 3. Lanzar contenedor de base de datos
Write-Host "3. Lanzando contenedor de base de datos PostgreSQL..." -ForegroundColor Yellow
$currentDir = Get-Location
docker run -d `
  --network network-equipo `
  --network-alias postgres `
  -v "${currentDir}:/mi-host" `
  --name db-equipo `
  -e POSTGRES_USER=mads `
  -e POSTGRES_PASSWORD=mads `
  -e POSTGRES_DB=mads `
  postgres:13

# 4. Esperar a que la base de datos esté lista
Write-Host "4. Esperando a que la base de datos esté lista..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

# 5. Restaurar esquema de datos si existe
if (Test-Path "sql/schema-1.3.0.sql") {
    Write-Host "5. Restaurando esquema de datos..." -ForegroundColor Yellow
    Get-Content "sql/schema-1.3.0.sql" | docker exec -i db-equipo psql -U mads mads
}

# 6. Lanzar aplicación
Write-Host "6. Lanzando aplicación ToDoList..." -ForegroundColor Yellow
docker run --rm `
  --network network-equipo `
  -p 8080:8080 `
  bryanhert/mads-todolist:1.3.0 `
  --spring.profiles.active=postgres-prod `
  --POSTGRES_HOST=postgres

Write-Host "=== Despliegue completado ===" -ForegroundColor Green
Write-Host "La aplicación está disponible en: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Para detener: docker stop db-equipo" -ForegroundColor Gray 