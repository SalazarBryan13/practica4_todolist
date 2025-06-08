package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AcercaDeController {

    @Autowired
    private ManagerUserSession managerUserSession;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/about")
    public String about(Model model) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            model.addAttribute("usuario", usuarioService.findById(idUsuarioLogeado));
        }
        model.addAttribute("idUsuarioLogeado", idUsuarioLogeado);
        return "about";
    }
} 