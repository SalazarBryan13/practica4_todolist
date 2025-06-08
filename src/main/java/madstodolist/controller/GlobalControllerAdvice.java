package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @ModelAttribute("usuario")
    public UsuarioData usuario() {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            return usuarioService.findById(idUsuarioLogeado);
        }
        return null;
    }

    @ModelAttribute("idUsuarioLogeado")
    public Long idUsuarioLogeado() {
        return managerUserSession.usuarioLogeado();
    }
} 