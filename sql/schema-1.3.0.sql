-- Esquema de datos versión 1.3.0
-- Generado automáticamente por Hibernate

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    bloqueado boolean DEFAULT false,
    fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP,
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

-- Secuencias para los IDs
CREATE SEQUENCE public.usuarios_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE public.tareas_id_seq START WITH 1 INCREMENT BY 1;

-- Índices para mejorar el rendimiento
CREATE INDEX idx_usuarios_email ON public.usuarios(email);

-- Datos iniciales de prueba
INSERT INTO public.usuarios (id, email, nombre, password, bloqueado, fecha_registro) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false, CURRENT_TIMESTAMP); 