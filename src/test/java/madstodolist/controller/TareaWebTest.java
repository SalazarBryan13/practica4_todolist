package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TareaWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private TareaService tareaService;

    @Autowired
    private UsuarioService usuarioService;

    private Map<String, Long> addUsuarioTareasBD() {
        // Crear usuario de prueba
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        usuarioService.registrar(usuarioData);

        // Obtener el usuario registrado para obtener su ID
        UsuarioData usuarioRegistrado = usuarioService.findByEmail("test@ua");

        // Crear tarea de prueba
        TareaData tareaData = new TareaData();
        tareaData.setTitulo("Tarea de prueba");
        tareaService.nuevaTareaUsuario(usuarioRegistrado.getId(), tareaData.getTitulo());

        // Obtener la tarea creada para obtener su ID
        TareaData tareaCreada = tareaService.allTareasUsuario(usuarioRegistrado.getId()).get(0);

        Map<String, Long> ids = new HashMap<>();
        ids.put("usuarioId", usuarioRegistrado.getId());
        ids.put("tareaId", tareaCreada.getId());
        return ids;
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void listaTareas() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Tarea de prueba")));
    }

    @Test
    public void listaTareasUsuarioNoLogeado() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void getNuevaTareaDevuelveForm() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas/nueva"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nueva tarea")));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void postNuevaTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(post("/usuarios/" + usuarioId + "/tareas/nueva")
                .param("titulo", "Nueva tarea"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/" + usuarioId + "/tareas"));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void getModificarTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(get("/tareas/" + tareaId + "/editar"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Modificar tarea")));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void postModificarTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(post("/tareas/" + tareaId + "/editar")
                .param("titulo", "Tarea modificada"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/" + usuarioId + "/tareas"));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void borrarTarea() throws Exception {
        Map<String, Long> ids = addUsuarioTareasBD();
        Long usuarioId = ids.get("usuarioId");
        Long tareaId = ids.get("tareaId");

        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn("Usuario Test");

        mockMvc.perform(delete("/tareas/" + tareaId))
                .andExpect(status().isOk());
    }
}
