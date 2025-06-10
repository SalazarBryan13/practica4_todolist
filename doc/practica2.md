# Documentación Técnica – Práctica 2

Nombre : Bryan Salazar

## Introducción

Durante la segunda práctica del proyecto ToDoList, se han implementado nuevas funcionalidades orientadas a la gestión de usuarios y la mejora de la experiencia de usuario. El desarrollo se ha gestionado mediante historias de usuario en Trello y issues en GitHub.

A continuación, se detallan los principales cambios y ampliaciones realizadas en la arquitectura, la lógica de negocio, la capa de presentación y la cobertura de pruebas.

---

## Funcionalidades implementadas

### 1. Página Acerca de

Se ha creado la página `/about`, accesible desde la barra de navegación y la pantalla de login. Esta página muestra información relevante sobre la aplicación, el desarrollador y la versión actual. El controlador `AcercaDeController` gestiona la lógica, añadiendo al modelo el usuario logeado (si existe) para personalizar la barra de navegación.

La vista `about.html` utiliza fragmentos Thymeleaf para la cabecera y la barra de navegación, mostrando los datos de versión y autor.

### 2. Barra de menú (Navbar)

Se ha implementado un fragmento Thymeleaf reutilizable (`fragments/navbar.html`) que muestra una barra de navegación superior en todas las páginas, excepto login y registro. La barra adapta su contenido según el estado de autenticación del usuario:
- Si no está logeado, muestra enlaces a login y registro.
- Si está logeado, muestra enlaces a tareas y un menú desplegable con el nombre del usuario y opciones de cuenta y cierre de sesión.

### 3. Listado y descripción de usuarios

El controlador `UsuarioController` gestiona dos endpoints protegidos para administradores:
- `/registrados`: muestra una tabla con todos los usuarios registrados, su rol (usuario o administrador), estado (activo o bloqueado) y permite al administrador bloquear/desbloquear usuarios.
- `/registrados/{id}`: muestra la información detallada de un usuario (nombre, email, fecha de nacimiento), excluyendo la contraseña.

Ambas vistas (`listadoUsuarios.html` y `descripcionUsuario.html`) están diseñadas con Bootstrap y fragmentos Thymeleaf para mantener la coherencia visual.

### 4. Usuario administrador y protección de páginas

Se ha añadido la posibilidad de que el primer usuario registrado pueda marcarse como administrador. El modelo `Usuario` incluye los campos `admin` y `bloqueado`, y la lógica de negocio en `UsuarioService` asegura que solo un usuario pueda tener el rol de administrador. El acceso a las páginas de gestión de usuarios está protegido y solo es posible para administradores, lanzando un error HTTP 403 en caso contrario.

### 5. Bloqueo y habilitación de usuarios

El administrador puede bloquear o habilitar usuarios desde el listado. Los usuarios bloqueados no pueden iniciar sesión; la lógica está implementada tanto en el servicio como en la integración con Spring Security. El estado se refleja visualmente en la tabla y mediante un botón de acción.

---

## Plantillas Thymeleaf y fragmentos

- `about.html`: Página Acerca de.
- `listadoUsuarios.html`: Listado de usuarios con acciones de administración.
- `descripcionUsuario.html`: Detalle de usuario.
- `fragments/navbar.html`: Barra de navegación reutilizable.
- Otros: formularios de login, registro, tareas, etc.

---

## Principales clases y métodos añadidos

- **Controladores:**
  - `AcercaDeController`: endpoint `/about`.
  - `UsuarioController`: endpoints `/registrados`, `/registrados/{id}` y acción de bloqueo.
- **Servicios:**
  - `UsuarioService`: gestión de login, registro, búsqueda, validación de administrador, bloqueo y desbloqueo de usuarios.
- **Modelos:**
  - `Usuario`: atributos `admin` y `bloqueado`, métodos de acceso y lógica de igualdad.
- **Repositorios:**
  - `UsuarioRepository`: métodos para búsqueda por email y listado completo.
- **DTOs:**
  - `UsuarioData`, `TareaData`, `LoginData`, `RegistroData`: facilitan la transferencia de datos entre capas.

---

## Pruebas implementadas

Se han añadido y ampliado tests automáticos :

- **Web:**  
  - `NavbarWebTest`: verifica la visualización dinámica de la barra de navegación según el estado de autenticación.
  - `UsuarioWebTest`: cubre login, registro, acceso a páginas protegidas, y acciones de administración.
- **Servicios:**  
  - `UsuarioServiceTest`: pruebas de registro, login, validación de administrador, bloqueo y desbloqueo.
- **Repositorios:**  
  - `UsuarioTest`, `TareaTest`: validan la persistencia y relaciones de los modelos.


---
## Metodología y Herramientas

Durante el desarrollo, utilicé Git y GitHub para el control de versiones. Cada nueva funcionalidad se desarrolló en una rama diferente, y luego se integró a la rama principal mediante pull requests. Los issues y el tablero de proyecto en GitHub me ayudaron a organizar y priorizar las tareas. Además, creé un tablero público en Trello para documentar las historias de usuario y su estado.
Para asegurar la calidad del código, implementé pruebas automáticas usando JUnit y las herramientas de testing de Spring Boot. Estas pruebas cubren tanto la lógica de negocio como los controladores web. Antes de cada entrega, ejecuté todos los tests para asegurarme de que la aplicación funcionaba correctamente.

## Despliegue y Docker

Una vez finalizado el desarrollo, empaqueté la aplicación en un archivo JAR usando Maven. Luego, creé una imagen Docker y la subí a Docker Hub, lo que facilita el despliegue en cualquier entorno. 

## Explicación de código

La arquitectura sigue el patrón MVC de Spring Boot, separando  la lógica de negocio (servicios), la persistencia (repositorios) y la presentación (controladores y vistas). Se ha hecho especial hincapié en la reutilización de fragmentos Thymeleaf y en la protección de rutas sensibles mediante validaciones en los controladores y servicios.

El uso de DTOs y ModelMapper facilita la transferencia de datos entre capas y mejora la mantenibilidad del código. Además, la cobertura de tests automáticos aseguran una garantia de las nuevas funcionalidades.

---

## Conclusión

 Se han seguido buenas prácticas de desarrollo , integración continua y pruebas automatizadas, lo que facilita la evolución futura del sistema y la colaboración en equipo. La documentación y la organización en Trello y GitHub han servido de apoyo para mantener la trazabilidad y la claridad en el desarrollo.

---

 

---

## Conclusión

 Se han seguido buenas prácticas de desarrollo , integración continua y pruebas automatizadas, lo que facilita la evolución futura del sistema y la colaboración en equipo. La documentación y la organización en Trello y GitHub han servido de apoyo para mantener la trazabilidad y la claridad en el desarrollo. 
