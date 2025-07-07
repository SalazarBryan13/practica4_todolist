-- Esquema de datos versión 1.2.0
-- Generado automáticamente por Hibernate

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    fecha_nacimiento date,
    admin boolean DEFAULT false NOT NULL,
    bloqueado boolean DEFAULT false NOT NULL,
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

-- Datos iniciales de prueba
INSERT INTO public.usuarios (id, email, nombre, password, admin, bloqueado) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false, false); 