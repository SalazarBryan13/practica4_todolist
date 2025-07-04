#!/bin/bash

# Script de despliegue en producción para ToDoList
# Versión: 1.3.0

echo "=== Despliegue en Producción ToDoList v1.3.0 ==="

# 1. Crear red de Docker
echo "1. Creando red de Docker..."
docker network create network-equipo 2>/dev/null || echo "Red ya existe"

# 2. Detener y eliminar contenedores existentes
echo "2. Deteniendo contenedores existentes..."
docker stop db-equipo 2>/dev/null || echo "Contenedor db-equipo no estaba ejecutándose"
docker rm db-equipo 2>/dev/null || echo "Contenedor db-equipo no existía"

# 3. Lanzar contenedor de base de datos
echo "3. Lanzando contenedor de base de datos PostgreSQL..."
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

# 5. Restaurar esquema de datos si existe
if [ -f "sql/schema-1.3.0.sql" ]; then
    echo "5. Restaurando esquema de datos..."
    docker exec -i db-equipo psql -U mads mads < sql/schema-1.3.0.sql
fi

# 6. Lanzar aplicación
echo "6. Lanzando aplicación ToDoList..."
docker run --rm \
  --network network-equipo \
  -p 8080:8080 \
  bryanhert/mads-todolist:1.3.0 \
  --spring.profiles.active=postgres-prod \
  --POSTGRES_HOST=postgres

echo "=== Despliegue completado ==="
echo "La aplicación está disponible en: http://localhost:8080"
echo "Para detener: docker stop db-equipo" 