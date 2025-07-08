-- Esquema de datos versiÃ³n 1.3.0
-- Generado automÃ¡ticamente por Hibernate

CREATE TABLE public.usuarios (
    id bigint NOT NULL,
    email character varying(255) NOT NULL,
    nombre character varying(255),
    password character varying(255),
    fecha_nacimiento date,
    admin boolean DEFAULT false NOT NULL,
    bloqueado boolean DEFAULT false NOT NULL,
    fecha_registro timestamp DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE public.tareas (
    id bigint NOT NULL,
    titulo character varying(255),
    descripcion text,
    fecha_limite timestamp,
    completada boolean DEFAULT false,
    prioridad character varying(255) DEFAULT 'MEDIA',
    usuario_id bigint,
    PRIMARY KEY (id),
    FOREIGN KEY (usuario_id) REFERENCES public.usuarios(id)
);

-- Secuencias para los IDs
CREATE SEQUENCE public.usuarios_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE public.tareas_id_seq START WITH 1 INCREMENT BY 1;

-- Configurar las secuencias para auto-incremento
ALTER TABLE public.usuarios ALTER COLUMN id SET DEFAULT nextval('public.usuarios_id_seq');
ALTER TABLE public.tareas ALTER COLUMN id SET DEFAULT nextval('public.tareas_id_seq');

-- Ãndices para mejorar el rendimiento
CREATE INDEX idx_usuarios_email ON public.usuarios(email);

-- Datos iniciales de prueba
-- ContraseÃ±a: "123" encriptada con BCrypt (hash correcto)
INSERT INTO public.usuarios (id, email, nombre, password, admin, bloqueado, fecha_registro) VALUES 
(1, 'user@ua', 'Usuario de Prueba', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', false, false, CURRENT_TIMESTAMP);

-- Actualizar las secuencias para que comiencen despuÃ©s del Ãºltimo ID usado
SELECT setval('public.usuarios_id_seq', (SELECT MAX(id) FROM public.usuarios));
SELECT setval('public.tareas_id_seq', 1); 
