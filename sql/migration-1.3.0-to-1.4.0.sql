-- Migración para versión 1.4.0
-- Nuevas funcionalidades: Prioridades de tareas y notificaciones mejoradas
-- Fecha: 2025-08-03
-- Autor: Equipo TodoList EPN

-- 1. Agregar columna de prioridad si no existe (compatible con H2 y PostgreSQL)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name='tareas' AND column_name='prioridad') THEN
        ALTER TABLE tareas ADD COLUMN prioridad VARCHAR(10) DEFAULT 'MEDIA';
        UPDATE tareas SET prioridad = 'MEDIA' WHERE prioridad IS NULL;
    END IF;
END $$;

-- 2. Crear índices para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_tareas_prioridad ON tareas(prioridad);
CREATE INDEX IF NOT EXISTS idx_tareas_usuario_prioridad ON tareas(usuario_id, prioridad);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario ON notificaciones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_fecha ON notificaciones(fecha_creacion);

-- 3. Actualizar datos existentes para consistencia
UPDATE tareas SET prioridad = 'MEDIA' WHERE prioridad IS NULL OR prioridad = '';

-- 4. Agregar restricciones de integridad
ALTER TABLE tareas ADD CONSTRAINT chk_prioridad 
CHECK (prioridad IN ('ALTA', 'MEDIA', 'BAJA'));

-- 5. Optimizar tabla de notificaciones
ALTER TABLE notificaciones ADD COLUMN IF NOT EXISTS leida BOOLEAN DEFAULT FALSE;
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida ON notificaciones(usuario_id, leida);

-- 6. Insertar datos de ejemplo si es necesario (solo en desarrollo)
-- Comentar en producción
/*
INSERT INTO usuarios (email, nombre, password, fecha_nacimiento, admin, bloqueado) VALUES
('demo@todolist.com', 'Usuario Demo', '$2a$10$7rRzqvWOV4RQbNqBsKlQI.hU2V4sW8vSYSAv6XzW3kVHGGG1aIhgO', '1990-01-01', false, false)
ON CONFLICT (email) DO NOTHING;
*/

-- 7. Crear vistas para reportes (opcional)
CREATE OR REPLACE VIEW v_tareas_por_prioridad AS
SELECT 
    u.nombre as usuario,
    t.prioridad,
    COUNT(*) as total_tareas
FROM tareas t
JOIN usuarios u ON t.usuario_id = u.id
GROUP BY u.nombre, t.prioridad
ORDER BY u.nombre, t.prioridad;

-- 8. Función para estadísticas (PostgreSQL)
CREATE OR REPLACE FUNCTION get_user_task_stats(user_id BIGINT)
RETURNS TABLE(
    total_tareas BIGINT,
    tareas_alta BIGINT,
    tareas_media BIGINT,
    tareas_baja BIGINT
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*) as total_tareas,
        COUNT(*) FILTER (WHERE prioridad = 'ALTA') as tareas_alta,
        COUNT(*) FILTER (WHERE prioridad = 'MEDIA') as tareas_media,
        COUNT(*) FILTER (WHERE prioridad = 'BAJA') as tareas_baja
    FROM tareas 
    WHERE usuario_id = user_id;
END;
$$ LANGUAGE plpgsql;

-- Log de migración
INSERT INTO schema_version (version, description, applied_date) VALUES 
('1.4.0', 'Prioridades de tareas y mejoras de rendimiento', NOW())
ON CONFLICT (version) DO UPDATE SET applied_date = NOW();

COMMIT;
