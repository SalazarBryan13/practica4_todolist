# Script de despliegue en desarrollo para ToDoList (Windows PowerShell)
# Versión: 1.3.0

Write-Host "=== Despliegue en Desarrollo ToDoList v1.3.0 ===" -ForegroundColor Green

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

# 5. Lanzar aplicación en modo desarrollo
Write-Host "5. Lanzando aplicación ToDoList en modo desarrollo..." -ForegroundColor Yellow
Write-Host "Usando perfil: postgres (desarrollo)" -ForegroundColor Cyan
Write-Host "DDL Auto: update (permite cambios automáticos)" -ForegroundColor Cyan

# Lanzar la aplicación con Maven dentro de la red de Docker
Write-Host "Compilando la aplicación..." -ForegroundColor Yellow
./mvnw clean package -DskipTests

Write-Host "Lanzando aplicación en contenedor Docker..." -ForegroundColor Yellow
docker run --rm `
  --network network-equipo `
  -p 8080:8080 `
  -v "${currentDir}:/mi-host" `
  --name app-equipo `
  openjdk:8-jdk-alpine `
  sh -c "java -Djava.security.egd=file:/dev/urandom -jar /mi-host/target/*.jar --spring.profiles.active=postgres --POSTGRES_HOST=postgres"

Write-Host "=== Desarrollo completado ===" -ForegroundColor Green
Write-Host "La aplicación está disponible en: http://localhost:8080" -ForegroundColor Cyan
Write-Host "Para detener: Ctrl+C y luego docker stop db-equipo" -ForegroundColor Gray 