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
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
public class NavbarWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    public void navbarMuestraNombreUsuarioCuandoEstaLogeado() throws Exception {
        // GIVEN
        // Un usuario logeado
        Long usuarioId = 1L;
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioId);

        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setId(usuarioId);
        usuarioData.setEmail("test@ua");
        usuarioData.setNombre("Usuario Test");
        when(usuarioService.findById(usuarioId)).thenReturn(usuarioData);

        // WHEN, THEN
        // La barra de navegación muestra el nombre del usuario
        this.mockMvc.perform(get("/usuarios/" + usuarioId + "/tareas"))
                .andExpect(content().string(containsString("Usuario Test")));
    }

    @Test
    public void navbarNoMuestraTareasCuandoNoEstaLogeado() throws Exception {
        // GIVEN
        // Usuario no logeado
        when(managerUserSession.usuarioLogeado()).thenReturn(null);

        // WHEN, THEN
        // La barra de navegación no muestra el enlace a tareas
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("ToDoList")))
                .andExpect(content().string(containsString("Tareas")));
    }
} 