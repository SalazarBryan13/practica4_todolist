package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class NavbarWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @WithMockUser(username = "test@ua", roles = {"USER"})
    public void navbarMuestraNombreUsuarioCuandoEstaLogeado() throws Exception {
        // Crear usuario de prueba
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        usuarioService.registrar(usuarioData);

        // Recuperar usuario registrado para obtener el ID
        UsuarioData usuarioRegistrado = usuarioService.findByEmail("test@ua");

        // Simular usuario logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioRegistrado.getId());
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn(usuarioRegistrado.getNombre());

        mockMvc.perform(get("/usuarios/" + usuarioRegistrado.getId() + "/tareas"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Usuario Test")))
                .andExpect(content().string(containsString("Tareas")));
    }

    @Test
    public void navbarNoMuestraTareasCuandoNoEstaLogeado() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registro")));
    }
} 