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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class UsuarioWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void getLoginDevuelveForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Iniciar sesión")))
                .andExpect(content().string(containsString("Email")))
                .andExpect(content().string(containsString("Contraseña")));
    }

    @Test
    public void getRegistroDevuelveForm() throws Exception {
        mockMvc.perform(get("/registro"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Registro nuevo usuario")))
                .andExpect(content().string(containsString("Correo electrónico")))
                .andExpect(content().string(containsString("Contraseña")));
    }

    @Test
    public void servicioLoginUsuarioOK() throws Exception {
        // Crear usuario de prueba
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        usuarioService.registrar(usuarioData);

        // Obtener el usuario registrado para obtener su ID
        UsuarioData usuarioRegistrado = usuarioService.findByEmail("test@ua");

        mockMvc.perform(post("/login")
                .param("eMail", "test@ua")
                .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/usuarios/" + usuarioRegistrado.getId() + "/tareas"));
    }

    @Test
    public void servicioLoginUsuarioNotFound() throws Exception {
        mockMvc.perform(post("/login")
                .param("eMail", "noexiste@ua")
                .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    public void servicioLoginUsuarioErrorPassword() throws Exception {
        // Crear usuario de prueba
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("test@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Usuario Test");
        usuarioService.registrar(usuarioData);

        mockMvc.perform(post("/login")
                .param("eMail", "test@ua")
                .param("password", "456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    @WithMockUser(username = "admin@ua", roles = {"ADMIN"})
    public void getListadoUsuariosDevuelveUsuarios() throws Exception {
        // Eliminar usuario admin previo si existe
        UsuarioData usuarioExistente = usuarioService.findByEmail("admin@ua");
        if (usuarioExistente != null) {
            // Si tienes un método para eliminar usuarios, úsalo aquí
            // usuarioService.eliminar(usuarioExistente.getId());
            // Si no, puedes dejarlo así, ya que el @Sql(clean-db.sql) debería limpiar la base
        }

        // Crear usuario admin de prueba
        UsuarioData usuarioData = new UsuarioData();
        usuarioData.setEmail("admin@ua");
        usuarioData.setPassword("123");
        usuarioData.setNombre("Admin Test");
        usuarioData.setAdmin(true);
        usuarioService.registrar(usuarioData);

        // Recuperar el usuario admin guardado
        UsuarioData usuarioAdmin = usuarioService.findByEmail("admin@ua");

        // Simular usuario admin logueado
        when(managerUserSession.usuarioLogeado()).thenReturn(usuarioAdmin.getId());
        when(managerUserSession.nombreUsuarioLogeado()).thenReturn(usuarioAdmin.getNombre());

        mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Test")));
    }
}
