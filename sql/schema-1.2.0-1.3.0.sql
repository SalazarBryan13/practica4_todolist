-- Script de migración de la versión 1.2.0 a la 1.3.0
-- Este script actualiza la base de datos de producción de la versión 1.2.0 a la 1.3.0

-- Añadir campo fecha_registro a la tabla usuarios
ALTER TABLE public.usuarios ADD COLUMN fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP;

-- Crear índice para mejorar el rendimiento de búsquedas por email
CREATE INDEX idx_usuarios_email ON public.usuarios(email);

-- Comentario: Esta migración añade un campo de auditoría a la tabla usuarios
-- El campo se añade con valor por defecto para mantener la compatibilidad 