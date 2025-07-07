# Aplicación inicial ToDoList

Aplicación ToDoList de la asignatura Metodologías Ágiles 2025-A EPN usando Spring Boot y plantillas Thymeleaf.

## Descripción funcional
Esta aplicación permite a los usuarios:
- Registrarse y autenticarse.
- Gestionar tareas personales (crear, editar, eliminar, listar).
- Visualizar una página "Acerca de" con información del equipo y la versión.
- Los administradores pueden ver el listado de usuarios, ver detalles y bloquear/habilitar usuarios.

## Estructura del proyecto
- `src/main/java/madstodolist/controller`: Controladores web (MVC).
- `src/main/java/madstodolist/service`: Lógica de negocio y servicios.
- `src/main/java/madstodolist/model`: Entidades JPA.
- `src/main/java/madstodolist/repository`: Repositorios de acceso a datos.
- `src/main/resources/templates`: Vistas Thymeleaf.
- `src/test/java/madstodolist`: Pruebas automáticas (controladores, servicios, repositorios).
- `doc/`: Documentación técnica y de la práctica.

## Enlace a tablero Trello (público)
El tablero Trello es público para su revisión:
https://trello.com/invite/b/683cd785831c50fb567856c0/ATTI2df8539ec62c0a21f78f18068105074c019CE665/todolist-epn

## Imagen Docker
La imagen está disponible en Docker Hub:
https://hub.docker.com/r/bryanhert/mads-todolist

## Requisitos

Necesitas tener instalado en tu sistema:
- Java 8
- Maven

## Ejecución

Puedes ejecutar la aplicación usando el _goal_ `run` del _plugin_ Maven de Spring Boot:

```
$ ./mvnw spring-boot:run 
```   

También puedes generar un `jar` y ejecutarlo:

```
$ ./mvnw package
$ java -jar target/epn-todolist-Bryan_Salazar-1.0.1-SNAPSHOT.jar
```

Una vez lanzada la aplicación puedes abrir un navegador y probar la página de inicio:
- [http://localhost:8080/login](http://localhost:8080/login)

## Ejecución con Docker
También puedes ejecutar la aplicación directamente con Docker:
```
docker run --rm -p 8080:8080 bryanhert/mads-todolist:1.0.1
```

## Credenciales de prueba
- Usuario de ejemplo: `user@ua`
- Contraseña: `123`
- (Puedes crear nuevos usuarios desde la página de registro)

## Cómo correr los tests
Para ejecutar todos los tests automáticos:
```
$ ./mvnw test
```

## Repositorio GitHub
Repositorio oficial del proyecto:
https://github.com/SalazarBryan13/TodoListSpringBoot

## Cómo contribuir
1. Haz un fork del repositorio.
2. Crea una rama para tu feature o corrección: `git checkout -b mi-feature`.
3. Realiza tus cambios y haz commit.
4. Haz push a tu rama: `git push origin mi-feature`.
5. Abre un Pull Request en GitHub.

## Licencia
Este proyecto es solo para fines educativos en la EPN. No se distribuye bajo una licencia específica.

## Contacto
Para dudas o soporte, contacta a: bryan.salazar@epn.edu.ec

- linea de prueba de conflicto, modificada por maicol