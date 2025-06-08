package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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

    @Test
    public void navbarEnLogin() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Registro")));
    }

    @Test
    public void navbarEnRegistro() throws Exception {
        this.mockMvc.perform(get("/registro"))
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Login")));
    }

    @Test
    public void navbarEnTareas() throws Exception {
        // GIVEN
        // Un usuario registrado
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        UsuarioData usuarioRegistrado = usuarioService.registrar(usuarioData);

        // WHEN, THEN
        // La barra de navegación muestra el nombre del usuario
        this.mockMvc.perform(get("/usuarios/" + usuarioRegistrado.getId() + "/tareas"))
                .andExpect(content().string(containsString("Usuario Test")));
    }

    @Test
    public void navbarEnAbout() throws Exception {
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Tareas")));
    }
} 