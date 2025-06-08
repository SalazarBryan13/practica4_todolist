# Documentación Técnica - Práctica 2

## Introducción

Este documento describe la arquitectura, las funcionalidades implementadas y las decisiones técnicas tomadas en el desarrollo de la aplicación ToDoListSpringBoot, siguiendo la metodología XP y las indicaciones de la práctica 3. El desarrollo se ha gestionado mediante ramas feature, issues y pull requests en GitHub, asegurando trazabilidad y control de versiones.

## Estructura del Proyecto

La aplicación está basada en Spring Boot y sigue una arquitectura MVC (Modelo-Vista-Controlador). El código fuente se organiza en los siguientes paquetes principales:

- `controller`: Controladores web que gestionan las rutas y la lógica de interacción con el usuario.
- `service`: Lógica de negocio y servicios de la aplicación.
- `model`: Entidades JPA que representan las tablas de la base de datos.
- `repository`: Interfaces para el acceso a datos usando Spring Data JPA.
- `dto`: Objetos de transferencia de datos para desacoplar la lógica de negocio de la presentación.

La configuración de la base de datos y otros parámetros se realiza en `application.properties`.

## Funcionalidades Implementadas

### 1. Gestión de Usuarios y Tareas

La aplicación permite el registro y autenticación de usuarios. Cada usuario puede crear, editar y eliminar tareas personales. Las relaciones entre usuarios y tareas están gestionadas mediante JPA, con una relación uno-a-muchos (un usuario puede tener muchas tareas).

### 2. Usuario Administrador

Se ha implementado la funcionalidad de usuario administrador, cumpliendo los siguientes requisitos:

- **Registro como administrador:** En la página de registro se muestra un checkbox para darse de alta como administrador, solo si no existe ya un administrador en el sistema.
- **Restricción de único administrador:** Si ya existe un usuario administrador, el checkbox no aparece para nuevos registros.
- **Acceso especial:** Al iniciar sesión, el usuario administrador es redirigido directamente a la lista de usuarios.
- **Controladores y servicios:** Se han modificado los controladores y servicios para soportar la lógica de verificación y registro de administradores.

### 3. Manejo de ramas y control de versiones

El desarrollo de la funcionalidad de administrador se realizó inicialmente en la rama `descripcion-usuario` por error, pero posteriormente se trasladó correctamente a la rama `administrador`, donde se realizó el commit y el push al repositorio remoto. Finalmente, la rama `administrador` fue fusionada con `main` para integrar los cambios en la rama principal.

### 4. Pruebas y validación

Se han ejecutado los tests automáticos proporcionados por el proyecto para asegurar que las nuevas funcionalidades no rompen la lógica existente. Además, se han realizado pruebas manuales para verificar el flujo de registro y acceso del usuario administrador, así como la restricción de único administrador.

## Decisiones Técnicas y Buenas Prácticas

- **Uso de DTOs:** Se emplean objetos DTO para transferir datos entre la capa de servicios y los controladores, evitando exponer directamente las entidades JPA y mejorando la seguridad y el desacoplamiento.
- **Inyección de dependencias:** Se utiliza la inyección de dependencias de Spring para gestionar los servicios y repositorios, facilitando la escalabilidad y el mantenimiento del código.
- **Transaccionalidad:** Los métodos de servicio que interactúan con la base de datos están anotados con `@Transactional` para asegurar la integridad de los datos y el correcto manejo de las relaciones lazy.
- **Control de acceso:** Se ha implementado lógica para asegurar que solo un usuario pueda ser administrador y que este tenga acceso especial a la lista de usuarios.
- **Control de versiones:** El uso de ramas feature y pull requests en GitHub ha permitido un desarrollo ordenado y colaborativo, siguiendo las mejores prácticas de integración continua.

## Conclusión

Hasta el momento, la aplicación cumple con los requisitos principales de la práctica, incluyendo la gestión de usuarios, tareas y la funcionalidad de usuario administrador. El flujo de trabajo basado en ramas y la integración continua han facilitado la trazabilidad y la calidad del desarrollo. Se recomienda continuar con la implementación de las siguientes funcionalidades obligatorias y opcionales, manteniendo la misma disciplina en el control de versiones y la documentación. 