package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import madstodolist.authentication.ManagerUserSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ManagerUserSession managerUserSession;

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        UsuarioData usuario = null;
        if (idUsuarioLogeado != null) {
            usuario = usuarioService.findById(idUsuarioLogeado);
        }
        if (usuario == null || !usuario.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos de administrador para ver el listado de usuarios");
        }
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listadoUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@PathVariable Long id, Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        UsuarioData usuarioLogeado = null;
        if (idUsuarioLogeado != null) {
            usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        }
        if (usuarioLogeado == null || !usuarioLogeado.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos de administrador para ver la descripción de usuario");
        }
        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "descripcionUsuario";
    }

    @PostMapping("/registrados/{id}/toggle-bloqueo")
    public String toggleBloqueo(@PathVariable Long id, @RequestParam boolean bloqueado) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        UsuarioData usuarioLogeado = null;
        if (idUsuarioLogeado != null) {
            usuarioLogeado = usuarioService.findById(idUsuarioLogeado);
        }
        if (usuarioLogeado == null || !usuarioLogeado.isAdmin()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene permisos de administrador para bloquear/desbloquear usuarios");
        }
        usuarioService.toggleBloqueo(id, bloqueado);
        return "redirect:/registrados";
    }
} 