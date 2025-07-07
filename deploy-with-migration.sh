#!/bin/bash

# Script de despliegue con migración de base de datos
# Este script demuestra cómo aplicar una migración en producción

echo "=== Despliegue con Migración de Base de Datos ==="

# 1. Crear red de Docker si no existe
echo "1. Creando red de Docker..."
docker network create network-equipo 2>/dev/null || echo "Red ya existe"

# 2. Detener contenedores existentes
echo "2. Deteniendo contenedores existentes..."
docker stop db-equipo app-equipo 2>/dev/null || echo "Contenedores no estaban ejecutándose"

# 3. Lanzar base de datos
echo "3. Lanzando base de datos PostgreSQL..."
docker run -d \
  --network network-equipo \
  --network-alias postgres \
  -v ${PWD}:/mi-host \
  --name db-equipo \
  -e POSTGRES_USER=mads \
  -e POSTGRES_PASSWORD=mads \
  -e POSTGRES_DB=mads \
  postgres:13

# 4. Esperar a que la base de datos esté lista
echo "4. Esperando a que la base de datos esté lista..."
sleep 10

# 5. Crear esquema inicial (versión 1.2.0)
echo "5. Creando esquema inicial (versión 1.2.0)..."
docker exec -i db-equipo psql -U mads mads < sql/schema-1.2.0.sql

# 6. Aplicar migración
echo "6. Aplicando migración 1.2.0 -> 1.3.0..."
docker exec -i db-equipo psql -U mads mads < sql/schema-1.2.0-1.3.0.sql

# 7. Verificar migración
echo "7. Verificando migración..."
docker exec -i db-equipo psql -U mads mads -c "\d usuarios"

# 8. Lanzar aplicación
echo "8. Lanzando aplicación..."
docker run -d \
  --network network-equipo \
  -p 8080:8080 \
  --name app-equipo \
  bryanhert/mads-todolist:1.3.0 \
  --spring.profiles.active=postgres-prod \
  --POSTGRES_HOST=postgres

echo "=== Despliegue completado ==="
echo "Aplicación disponible en: http://localhost:8080"
echo "Base de datos: postgres:5432"
echo ""
echo "Para verificar la migración:"
echo "docker exec -it db-equipo psql -U mads mads -c 'SELECT * FROM usuarios;'" 