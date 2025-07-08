package madstodolist.service;

import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);

    public void crearNotificacion(Tarea tarea, Usuario usuario, String mensaje) {
        // Por ahora solo logueamos la notificación
        // En el futuro se podría implementar notificaciones por email, push, etc.
        logger.info("Notificación para usuario {}: {}", usuario.getEmail(), mensaje);
    }
} 