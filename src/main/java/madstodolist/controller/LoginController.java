package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.LoginData;
import madstodolist.dto.RegistroData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.service.UsuarioService.LoginStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private ManagerUserSession managerUserSession;

    @GetMapping("/")
    public String home(Model model) {
        logger.debug("Redirigiendo a la página de login");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        logger.debug("Accediendo al formulario de login");
        model.addAttribute("loginData", new LoginData());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginData loginData, Model model, HttpSession session) {
        logger.debug("Intento de login para usuario: {}", loginData.geteMail());
        
        LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());
        
        if (loginStatus == LoginStatus.LOGIN_OK) {
            logger.debug("Login exitoso para usuario: {}", loginData.geteMail());
            UsuarioData usuario = usuarioService.findByEmail(loginData.geteMail());
            if (usuario != null) {
                managerUserSession.logearUsuario(usuario.getId(), usuario.getNombre());
                return "redirect:/usuarios/" + usuario.getId() + "/tareas";
            }
            return "redirect:/login";
        } else {
            logger.error("Error en login para usuario: {}. Estado: {}", loginData.geteMail(), loginStatus);
            if (loginStatus == LoginStatus.USER_NOT_FOUND) {
                model.addAttribute("error", "No existe usuario");
            } else if (loginStatus == LoginStatus.ERROR_PASSWORD) {
                model.addAttribute("error", "Contraseña incorrecta");
            } else {
                model.addAttribute("error", "Error en el login");
            }
            return "login";
        }
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        logger.debug("Accediendo al formulario de registro");
        model.addAttribute("registroData", new RegistroData());
        return "registro";
    }

    @PostMapping("/registro")
    public String registro(@Valid @ModelAttribute RegistroData registroData, Model model) {
        logger.debug("Intento de registro para usuario: {}", registroData.geteMail());
        
        try {
            if (registroData.geteMail() == null || registroData.geteMail().trim().isEmpty()) {
                model.addAttribute("error", "El email es obligatorio");
                return "registro";
            }
            
            if (registroData.getPassword() == null || registroData.getPassword().trim().isEmpty()) {
                model.addAttribute("error", "La contraseña es obligatoria");
                return "registro";
            }
            
            UsuarioData usuarioData = new UsuarioData();
            usuarioData.setEmail(registroData.geteMail());
            usuarioData.setPassword(registroData.getPassword());
            usuarioData.setNombre(registroData.getNombre());
            
            UsuarioData usuario = usuarioService.registrar(usuarioData);
            logger.debug("Registro exitoso para usuario: {}", usuario.getEmail());
            return "redirect:/login";
        } catch (Exception e) {
            logger.error("Error en registro para usuario: {}. Error: {}", registroData.geteMail(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        logger.debug("Cerrando sesión");
        managerUserSession.logout();
        return "redirect:/login";
    }

    @GetMapping("/usuarios/{id}/perfil")
    public String perfilUsuario(@PathVariable(value = "id") Long idUsuario, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        
        if (idUsuarioLogeado == null) {
            return "redirect:/login";
        }

        if (!idUsuarioLogeado.equals(idUsuario)) {
            return "redirect:/usuarios/" + idUsuarioLogeado + "/perfil";
        }

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }
}
