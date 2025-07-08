CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT IDENTITY PRIMARY KEY,
    email VARCHAR(255) UNIQUE,
    nombre VARCHAR(255),
    password VARCHAR(255),
    fecha_nacimiento DATE,
    admin BOOLEAN,
    bloqueado BOOLEAN,
    fecha_registro TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tareas (
    id BIGINT IDENTITY PRIMARY KEY,
    titulo VARCHAR(255),
    usuario_id BIGINT REFERENCES usuarios,
    prioridad VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS notificaciones (
    id BIGINT IDENTITY PRIMARY KEY,
    mensaje VARCHAR(255) NOT NULL,
    fecha_notificacion TIMESTAMP NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT FALSE,
    tarea_id BIGINT REFERENCES tareas,
    usuario_id BIGINT REFERENCES usuarios
); 