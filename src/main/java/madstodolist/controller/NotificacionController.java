package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.NotificacionService;
import madstodolist.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NotificacionController {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionController.class);

    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private ManagerUserSession managerUserSession;
    
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/notificaciones")
    public String mostrarNotificaciones(Model model) {
        logger.debug("Accediendo a la página de notificaciones");
        
        // Obtener usuario logueado
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            logger.debug("Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }

        // Obtener datos del usuario
        UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
        if (usuario == null) {
            logger.debug("Usuario no encontrado, redirigiendo a login");
            return "redirect:/login";
        }

        // Lista de notificaciones de ejemplo (en una implementación real vendría de la base de datos)
        List<String> notificaciones = new ArrayList<>();
        notificaciones.add("Bienvenido a ToDoList, " + usuario.getNombre() + "!");
        notificaciones.add("Tu cuenta fue creada exitosamente.");
        notificaciones.add("Puedes comenzar a crear tus tareas.");
        notificaciones.add("Recuerda que puedes asignar prioridades a tus tareas.");

        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("usuario", usuario);
        
        return "notificaciones";
    }

    @GetMapping("/usuarios/{id}/notificaciones")
    public String mostrarNotificacionesUsuario(@PathVariable Long id, Model model) {
        logger.debug("Accediendo a las notificaciones del usuario {}", id);
        
        // Obtener usuario logueado
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado == null) {
            logger.debug("Usuario no autenticado, redirigiendo a login");
            return "redirect:/login";
        }

        // Verificar que el usuario accede a sus propias notificaciones
        if (!idUsuarioLogeado.equals(id)) {
            logger.debug("Usuario {} intentando acceder a notificaciones de usuario {}", idUsuarioLogeado, id);
            return "redirect:/notificaciones";
        }

        // Obtener datos del usuario
        UsuarioData usuario = usuarioService.findById(id);
        if (usuario == null) {
            logger.debug("Usuario no encontrado, redirigiendo a login");
            return "redirect:/login";
        }

        // Lista de notificaciones específicas del usuario
        List<String> notificaciones = new ArrayList<>();
        notificaciones.add("Hola " + usuario.getNombre() + ", aquí tienes tus notificaciones:");
        notificaciones.add("Última sesión: " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        notificaciones.add("Estado de cuenta: Activa");
        notificaciones.add("Puedes ver tus tareas en la sección correspondiente.");

        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("usuario", usuario);
        
        return "notificaciones";
    }
} 