#!/bin/bash
# Script de backup automático para base de datos PostgreSQL
# Versión: 1.4.0
# Fecha: 2025-08-03

# Configuración
DB_HOST="${POSTGRES_HOST:-localhost}"
DB_PORT="${POSTGRES_PORT:-5432}"
DB_NAME="${POSTGRES_DB:-todolistdb}"
DB_USER="${POSTGRES_USER:-todolist_user}"
BACKUP_DIR="./sql/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="backup_todolist_${DATE}.sql"

# Crear directorio de backups si no existe
mkdir -p "$BACKUP_DIR"

echo "🔄 Iniciando backup de base de datos..."
echo "   Servidor: $DB_HOST:$DB_PORT"
echo "   Base de datos: $DB_NAME"
echo "   Fecha: $(date)"

# Realizar backup completo
pg_dump -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" \
        --no-password --verbose --clean --if-exists \
        --format=plain --file="$BACKUP_DIR/$BACKUP_FILE"

if [ $? -eq 0 ]; then
    echo "✅ Backup completado exitosamente: $BACKUP_DIR/$BACKUP_FILE"
    
    # Comprimir backup
    gzip "$BACKUP_DIR/$BACKUP_FILE"
    echo "📦 Backup comprimido: $BACKUP_DIR/$BACKUP_FILE.gz"
    
    # Limpiar backups antiguos (mantener últimos 7)
    find "$BACKUP_DIR" -name "backup_todolist_*.sql.gz" -type f -mtime +7 -delete
    echo "🧹 Backups antiguos eliminados (>7 días)"
    
    # Mostrar tamaño del backup
    BACKUP_SIZE=$(du -h "$BACKUP_DIR/$BACKUP_FILE.gz" | cut -f1)
    echo "📊 Tamaño del backup: $BACKUP_SIZE"
    
else
    echo "❌ Error durante el backup"
    exit 1
fi

echo "✨ Proceso de backup finalizado"
