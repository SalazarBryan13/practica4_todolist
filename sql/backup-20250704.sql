-- Copia de seguridad de la base de datos mads
-- Fecha: 4 de julio de 2025
-- Versión: 1.3.0

-- Esquema de datos
CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    bloqueado boolean DEFAULT false,
    PRIMARY KEY (id)
);

CREATE TABLE public.tareas (
    id bigint NOT NULL,
    titulo character varying(255),
    descripcion text,
    fecha_limite timestamp,
    completada boolean DEFAULT false,
    usuario_id bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id)
);

-- Secuencias
CREATE SEQUENCE public.usuarios_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE public.tareas_id_seq START WITH 1 INCREMENT BY 1;

-- Datos de usuarios
INSERT INTO public.usuarios (id, email, nombre, password, bloqueado) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false);

-- Datos de tareas (si existen)
-- INSERT INTO public.tareas (id, titulo, descripcion, fecha_limite, completada, usuario_id) VALUES 
-- (1, 'Tarea de ejemplo', 'Descripción de la tarea', '2025-07-10 12:00:00', false, 1); 