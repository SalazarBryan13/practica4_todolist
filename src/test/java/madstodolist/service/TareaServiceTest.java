package madstodolist.service;

import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Hemos eliminado todos los @Transactional de los tests
// y usado un script para limpiar la BD de test después de
// cada test
// https://dev.to/henrykeys/don-t-use-transactional-in-tests-40eb

@SpringBootTest
@Sql(scripts = "/clean-db.sql")
public class TareaServiceTest {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    // Método para inicializar los datos de prueba en la BD
    // Devuelve un mapa con los identificadores del usuario y de la primera tarea añadida
    Map<String, Long> addUsuarioTareasBD() {
        UsuarioData usuario = new UsuarioData();
        usuario.setEmail("user@ua");
        usuario.setPassword("123");
        usuario.setNombre("Usuario Test");

        // Añadimos un usuario a la base de datos
        UsuarioData usuarioNuevo = usuarioService.registrar(usuario);

        // Y añadimos dos tareas asociadas a ese usuario
        TareaData tarea1 = tareaService.nuevaTareaUsuario(usuarioNuevo.getId(), "Lavar coche");
        tareaService.nuevaTareaUsuario(usuarioNuevo.getId(), "Renovar DNI");

        // Devolvemos los ids del usuario y de la primera tarea añadida
        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuarioNuevo.getId());
        ids.put("tareaId", tarea1.getId());
        return ids;
    }

    @Test
    public void nuevaTareaUsuario() {
        // GIVEN
        // Un usuario en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        // WHEN
        // Añadimos una nueva tarea al usuario
        TareaData tarea = tareaService.nuevaTareaUsuario(usuarioId, "Nueva tarea");

        // THEN
        // La tarea se crea correctamente con los datos proporcionados
        assertThat(tarea).isNotNull();
        assertThat(tarea.getTitulo()).isEqualTo("Nueva tarea");
        assertThat(tarea.getId()).isNotNull();
    }

    @Test
    public void nuevaTareaUsuarioNoExiste() {
        // WHEN, THEN
        // Si intentamos crear una tarea para un usuario que no existe,
        // se produce una excepción de tipo TareaServiceException
        assertThatThrownBy(() -> {
            tareaService.nuevaTareaUsuario(999L, "Nueva tarea");
        }).isInstanceOf(TareaServiceException.class)
          .hasMessageContaining("no existe");
    }

    @Test
    public void allTareasUsuario() {
        // GIVEN
        // Un usuario con dos tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        // WHEN
        // Recuperamos todas las tareas del usuario
        List<TareaData> tareas = tareaService.allTareasUsuario(usuarioId);

        // THEN
        // Se obtienen las dos tareas creadas
        assertThat(tareas).hasSize(2);
        assertThat(tareas).extracting("titulo")
                .containsExactlyInAnyOrder("Lavar coche", "Renovar DNI");
    }

    @Test
    public void allTareasUsuarioNoExiste() {
        // WHEN, THEN
        // Si intentamos recuperar las tareas de un usuario que no existe,
        // se produce una excepción de tipo TareaServiceException
        assertThatThrownBy(() -> {
            tareaService.allTareasUsuario(999L);
        }).isInstanceOf(TareaServiceException.class)
          .hasMessageContaining("no existe");
    }

    @Test
    public void findById() {
        // GIVEN
        // Un usuario con tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long tareaId = ids.get("tareaId");

        // WHEN
        // Buscamos una tarea por su ID
        TareaData tarea = tareaService.findById(tareaId);

        // THEN
        // Se encuentra la tarea correcta
        assertThat(tarea).isNotNull();
        assertThat(tarea.getId()).isEqualTo(tareaId);
        assertThat(tarea.getTitulo()).isEqualTo("Lavar coche");
    }

    @Test
    public void findByIdNoExiste() {
        // WHEN
        // Buscamos una tarea que no existe
        TareaData tarea = tareaService.findById(999L);

        // THEN
        // Se devuelve null
        assertThat(tarea).isNull();
    }

    @Test
    public void modificaTarea() {
        // GIVEN
        // Un usuario con tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long tareaId = ids.get("tareaId");

        // WHEN
        // Modificamos el título de la tarea
        TareaData tareaModificada = tareaService.modificaTarea(tareaId, "Nuevo título");

        // THEN
        // La tarea se modifica correctamente
        assertThat(tareaModificada).isNotNull();
        assertThat(tareaModificada.getId()).isEqualTo(tareaId);
        assertThat(tareaModificada.getTitulo()).isEqualTo("Nuevo título");
    }

    @Test
    public void modificaTareaNoExiste() {
        // WHEN, THEN
        // Si intentamos modificar una tarea que no existe,
        // se produce una excepción de tipo TareaServiceException
        assertThatThrownBy(() -> {
            tareaService.modificaTarea(999L, "Nuevo título");
        }).isInstanceOf(TareaServiceException.class)
          .hasMessageContaining("No existe tarea");
    }

    @Test
    public void borraTarea() {
        // GIVEN
        // Un usuario con tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long tareaId = ids.get("tareaId");

        // WHEN
        // Borramos la tarea
        tareaService.borraTarea(tareaId);

        // THEN
        // La tarea ya no existe
        assertThat(tareaService.findById(tareaId)).isNull();
    }

    @Test
    public void borraTareaNoExiste() {
        // WHEN, THEN
        // Si intentamos borrar una tarea que no existe,
        // se produce una excepción de tipo TareaServiceException
        assertThatThrownBy(() -> {
            tareaService.borraTarea(999L);
        }).isInstanceOf(TareaServiceException.class)
          .hasMessageContaining("No existe tarea");
    }

    @Test
    public void usuarioContieneTarea() {
        // GIVEN
        // Un usuario con tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");

        // WHEN
        // Comprobamos si el usuario contiene la tarea
        boolean contiene = tareaService.usuarioContieneTarea(usuarioId, tareaId);

        // THEN
        // El usuario contiene la tarea
        assertThat(contiene).isTrue();
    }

    @Test
    public void usuarioNoContieneTarea() {
        // GIVEN
        // Un usuario con tareas en la BD
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        // WHEN, THEN
        // Si intentamos comprobar si el usuario contiene una tarea que no existe,
        // se produce una excepción de tipo TareaServiceException
        assertThatThrownBy(() -> {
            tareaService.usuarioContieneTarea(usuarioId, 999L);
        }).isInstanceOf(TareaServiceException.class)
          .hasMessageContaining("No existe tarea o usuario");
    }
}
