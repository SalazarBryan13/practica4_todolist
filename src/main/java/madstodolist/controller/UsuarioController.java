package madstodolist.controller;

import madstodolist.dto.UsuarioData;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registrados")
    public String listadoUsuarios(Model model) {
        List<UsuarioData> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "listadoUsuarios";
    }

    @GetMapping("/registrados/{id}")
    public String descripcionUsuario(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        UsuarioData usuario = usuarioService.findById(id);
        model.addAttribute("usuario", usuario);
        return "descripcionUsuario";
    }
} 