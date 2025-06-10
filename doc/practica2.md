# Documentación Técnica – Práctica 2

Nombre: Bryan Salazar

## Introducción

Durante la segunda práctica del proyecto ToDoList, se han implementado nuevas funcionalidades orientadas a la gestión de usuarios y la mejora de la experiencia de usuario. El desarrollo se ha gestionado mediante historias de usuario en Trello y issues en GitHub.

A continuación, se detallan los principales cambios  realizados en la arquitectura, la lógica de negocio, la capa de presentación y la cobertura de pruebas.

## Funcionalidades implementadas

### Página Acerca de
Se implementó una página accesible desde la barra de navegación y la pantalla de login, que muestra información sobre la aplicación, su desarrollador y la versión actual. 

### Barra de navegación
La barra de navegación está diseñada para aparecer en todas las páginas, excepto en las de login y registro. Su contenido varía según el estado del usuario: si no está autenticado, muestra opciones para iniciar sesión o registrarse; si está autenticado, incluye enlaces a las tareas y un menú con el nombre del usuario, opciones de cuenta y cierre de sesión.

### Listado y detalle de usuarios
Se desarrolló una funcionalidad exclusiva para administradores que permite visualizar una tabla con todos los usuarios, indicando su rol (usuario o administrador) y estado (activo o bloqueado), con la opción de bloquear o desbloquear cuentas. También se puede acceder a una vista detallada de cada usuario, mostrando nombre, email y fecha de nacimiento, sin incluir datos sensibles.

### Usuario administrador y protección
Se habilitó la opción de designar al primer usuario registrado como administrador. Solo este rol puede acceder a las funciones de gestión de usuarios, con restricciones que generan un error de acceso denegado para otros usuarios.

### Bloqueo y habilitación de usuarios
Se implementó un sistema para que los administradores puedan bloquear o habilitar usuarios desde la tabla. Los usuarios bloqueados no pueden iniciar sesión, y este estado se refleja visualmente con un botón que permite modificarlo.

## Nuevas Clases y Métodos Implementados

### Controladores
Los controladores manejan las peticiones web y conectan la interfaz de usuario con la lógica de negocio:

- `AcercaDeController`: 
  - `@GetMapping("/about")`: Renderiza la página Acerca de
  - Este controlador añade información del usuario actual a cualquier página mediante `@ModelAttribute`, permitiendo mostrar el nombre del usuario en la barra de navegación.

- `UsuarioController`: 
  - `@GetMapping("/registrados")`: Listado de usuarios (admin)
  - `@GetMapping("/registrados/{id}")`: Detalle de usuario
  - `@PostMapping("/registrados/{id}/bloquear")`: Bloqueo/desbloqueo
  - Este controlador gestiona el acceso a las funcionalidades de administración. Implementa protección de rutas mediante Spring Security, devolviendo un error 403 (Forbidden) para usuarios no autorizados.

- `LoginController`:
  - `@PostMapping("/login")`: Autenticación
  - `@PostMapping("/registro")`: Registro de usuarios
  - Gestiona la autenticación y registro de usuarios, validando credenciales y manejando el proceso de inicio de sesión.

### Servicios
Los servicios implementan la lógica de negocio principal de la aplicación:

- `UsuarioService`:
  - `registrar(UsuarioData)`: Registro con validación
    - Valida la disponibilidad del email y encripta la contraseña antes de almacenar los datos del usuario.
  - `findByEmail(String)`: Búsqueda por email
    - Realiza búsquedas eficientes de usuarios mediante el email, que está indexado en la base de datos.
  - `findAllUsuarios()`: Listado completo
    - Proporciona acceso a la lista completa de usuarios para el panel de administración.
  - `bloquearUsuario(Long)`: Gestión de bloqueo
    - Implementa la funcionalidad de bloqueo y desbloqueo de usuarios mediante transacciones seguras.

### Modelos y DTOs
Los modelos representan los datos en la base de datos, mientras que los DTOs facilitan la transferencia de datos entre capas:

- `Usuario`: 
  ```java
  @Entity
  public class Usuario {
      private String email;
      private String password;
      private String nombre;
      private boolean admin;
      private boolean bloqueado;
      // Getters y setters
  }
  ```
  Esta clase define la estructura de datos del usuario en la base de datos:
  - `email`: Identificador único del usuario
  - `password`: Contraseña encriptada
  - `nombre`: Nombre mostrado en la interfaz
  - `admin`: Indica si es administrador
  - `bloqueado`: Indica si el usuario está bloqueado

- DTOs:
  - `UsuarioData`: Datos de usuario (sin password)
    - Contiene los datos del usuario para mostrar en la interfaz, excluyendo información sensible.
  - `LoginData`: Credenciales
    - Almacena las credenciales necesarias para el proceso de autenticación.
  - `RegistroData`: Datos de registro
    - Contiene los datos necesarios para el registro de nuevos usuarios.

## Plantillas Thymeleaf Añadidas

### Páginas Principales
Las plantillas implementan la interfaz de usuario con Thymeleaf:

- `about.html`: Página Acerca de
  - Muestra información sobre la aplicación.
- `listadoUsuarios.html`: Tabla de usuarios 
  - Presenta una tabla interactiva con la lista de usuarios y controles de administración.
- `descripcionUsuario.html`: Detalle de usuario
  - Muestra la información detallada de un usuario .

- `fragments/navbar.html`: Barra de navegación reutilizable.

### Fragmentos
Los fragmentos son componentes HTML reutilizables:

- `fragments/navbar.html`: Barra de navegación reutilizable
  ```html
  <nav th:fragment="navbar" class="navbar navbar-expand-lg navbar-dark bg-dark">
      <!-- Contenido dinámico según autenticación -->
      <div th:if="${idUsuarioLogeado != null}">
          <!-- Menú usuario autenticado -->
      </div>
      <div th:if="${idUsuarioLogeado == null}">
          <!-- Menú usuario no autenticado -->
      </div>
  </nav>
  ```
  La barra de navegación se adapta dinámicamente según el estado de autenticación:
  - Muestra el nombre y opciones del usuario cuando está autenticado
  - Presenta opciones de login y registro para usuarios no autenticados
  - Implementa un diseño responsive con Bootstrap

## Tests Implementados

### Tests Web
Los tests web verifican el funcionamiento de la interfaz:

- `NavbarWebTest`: 
  ```java
  @Test
  @WithMockUser(username = "test@ua", roles = {"USER"})
  public void navbarMuestraNombreUsuarioCuandoEstaLogeado() {
      // Verifica visualización del nombre de usuario
  }
  ```
  Verifica la correcta visualización del nombre de usuario en la barra de navegación.

- `UsuarioWebTest`: 
  - Login/registro
  - Acceso a páginas protegidas
  - Acciones de administrador
  Asegura el correcto funcionamiento de:
  - Proceso de registro y autenticación
  - Protección de rutas administrativas
  - Funcionalidades de administración

### Tests de Servicio
Los tests de servicio validan la lógica de negocio:

- `UsuarioServiceTest`:
  - Registro de usuarios
  - Validación de administrador
  - Bloqueo/desbloqueo
  Verifica:
  - El correcto registro de usuarios
  - La restricción a un único administrador
  - El funcionamiento del sistema de bloqueo

## Código Relevante

Implementación del bloqueo de usuarios en `UsuarioService`:
```java
@Override
@Transactional(readOnly = true)
public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
    if (!usuario.isPresent()) {
        throw new UsernameNotFoundException("Usuario no encontrado");
    }
    if (usuario.get().isBloqueado()) {
        throw new UsernameNotFoundException("Usuario bloqueado");
    }
    return new User(
        usuario.get().getEmail(),
        usuario.get().getPassword(),
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
}
```

Características del código:
1. Implementa la integración con Spring Security
   - Este método proporciona la autenticación personalizada para Spring Security, verificando las credenciales del usuario.
2. Maneja el bloqueo de usuarios a nivel de autenticación
   - Implementa la lógica de bloqueo durante el proceso de autenticación, denegando el acceso a usuarios bloqueados.
3. Utiliza transacciones para operaciones de lectura
   - La anotación `@Transactional(readOnly = true)` optimiza el rendimiento de las operaciones de lectura.
4. Sigue el patrón de diseño Repository
   - Utiliza el repositorio para acceder a la base de datos de manera encapsulada .

## Metodología y Herramientas

- Control de versiones: Git/GitHub
  - Facilita el control de cambios y la colaboración en el desarrollo.
- Gestión de tareas: Trello
  - Organiza y prioriza las tareas del proyecto de manera visual.
- Tests: JUnit + Spring Boot Test
  - Proporciona herramientas para la verificación automatizada del código.
- Despliegue: Docker
  - Una vez finalizado el desarrollo, empaqueté la aplicación en un archivo JAR usando Maven. Luego, creé una imagen Docker y la subí a Docker Hub, lo que facilita el despliegue en cualquier entorno.

## Conclusión

Se han seguido buenas prácticas de desarrollo , integración continua y pruebas automatizadas, lo que facilita la evolución futura del sistema y la colaboración en equipo, ademas de asegurarme de que cada nueva funcionalidad no afectara lo anterior. Esto facilitó mucho el mantenimiento del proyecto y me dio un apoyo para seguir construyendo sobre él en el futuro. También me ayudó a organizar mejor mi trabajo usando herramientas como Trello y GitHub, que me permitieron tener un seguimiento claro de lo que iba haciendo y de lo que faltaba por completar.

---

 
---

 



