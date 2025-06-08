package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class NavbarTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void navbarEnLogin() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Registro")));
    }

    @Test
    public void navbarEnRegistro() throws Exception {
        this.mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void navbarEnTareas() throws Exception {
        // GIVEN
        // Un usuario registrado
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuarioData);

        // Configurar el ManagerUserSession para que devuelva el ID del usuario
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn(usuarioRegistrado.getNombre());

        // WHEN, THEN
        // La barra de navegación muestra el nombre del usuario
        this.mockMvc.perform(get("/usuarios/" + usuarioRegistrado.getId() + "/tareas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuario Test")));
    }

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void navbarEnAbout() throws Exception {
        // GIVEN
        // Un usuario registrado
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuarioData);

        // Configurar el ManagerUserSession para que devuelva el ID del usuario
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn(usuarioRegistrado.getNombre());

        this.mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Tareas")));
    }
} 