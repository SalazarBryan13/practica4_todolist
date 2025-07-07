-- Script de prueba para verificar la migración 1.2.0 -> 1.3.0
-- Este script simula el proceso completo de migración

-- 1. Crear base de datos con esquema 1.2.0
\echo 'Paso 1: Creando base de datos con esquema 1.2.0...'
\i sql/schema-1.2.0.sql

-- 2. Verificar estructura inicial
\echo 'Paso 2: Verificando estructura inicial...'
\d usuarios

-- 3. Aplicar migración
\echo 'Paso 3: Aplicando migración 1.2.0 -> 1.3.0...'
\i sql/schema-1.2.0-1.3.0.sql

-- 4. Verificar estructura final
\echo 'Paso 4: Verificando estructura final...'
\d usuarios

-- 5. Verificar que los datos existentes se mantienen
\echo 'Paso 5: Verificando datos...'
SELECT id, email, nombre, fecha_registro FROM usuarios;

-- 6. Insertar nuevo usuario para probar el campo fecha_registro
\echo 'Paso 6: Probando inserción con campo fecha_registro...'
INSERT INTO usuarios (email, nombre, password) 
VALUES ('test@example.com', 'Usuario Test', 'password123');

-- 7. Verificar inserción exitosa
SELECT id, email, nombre, fecha_registro FROM usuarios WHERE email = 'test@example.com';

\echo 'Migración completada exitosamente!' 