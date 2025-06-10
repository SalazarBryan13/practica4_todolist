package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.dto.TareaData;
import madstodolist.dto.UsuarioData;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;

// Controlador encargado de gestionar las tareas de los usuarios.
// Incluye endpoints para crear, listar, editar y borrar tareas.
@Controller
public class TareaController {

    private static final Logger logger = LoggerFactory.getLogger(TareaController.class);

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ManagerUserSession managerUserSession;

    /**
     * Verifica que el usuario logeado coincide con el usuario de la URL.
     * Lanza una excepción si no coincide.
     */
    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    /**
     * Muestra el formulario para crear una nueva tarea para un usuario.
     */
    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "formNuevaTarea";
    }

    /**
     * Procesa la creación de una nueva tarea para un usuario.
     */
    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo());
        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
     }

    /**
     * Muestra el listado de tareas de un usuario.
     */
    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        UsuarioData usuario = usuarioService.findById(idUsuario);
        List<TareaData> tareas = tareaService.allTareasUsuario(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tareas", tareas);
        return "listaTareas";
    }

    /**
     * Muestra el formulario para editar una tarea existente.
     */
    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, Model model, HttpSession session) {
        logger.debug("Entrando a formEditaTarea con idTarea: {}", idTarea);
        TareaData tarea = tareaService.findById(idTarea);
        logger.debug("Tarea encontrada: {}", tarea);
        if (tarea == null) {
            logger.error("No se encontró la tarea con id: {}", idTarea);
            throw new TareaNotFoundException();
        }
        logger.debug("UsuarioId de la tarea: {}", tarea.getUsuarioId());
        comprobarUsuarioLogeado(tarea.getUsuarioId());
        model.addAttribute("tarea", tarea);
        logger.debug("Atributo 'tarea' añadido al modelo: {}", tarea);
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (idUsuarioLogeado != null) {
            UsuarioData usuario = usuarioService.findById(idUsuarioLogeado);
            model.addAttribute("usuario", usuario);
        }
        return "formEditarTarea";
    }

    /**
     * Procesa la edición de una tarea existente.
     */
    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tarea,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        logger.debug("Entrando a grabaTareaModificada con idTarea: {} y titulo: {}", idTarea, tarea.getTitulo());
        TareaData tareaExistente = tareaService.findById(idTarea);
        logger.debug("Tarea existente encontrada: {}", tareaExistente);
        if (tareaExistente == null) {
            logger.error("No se encontró la tarea con id: {}", idTarea);
            throw new TareaNotFoundException();
        }
        Long idUsuario = tareaExistente.getUsuarioId();
        logger.debug("UsuarioId de la tarea existente: {}", idUsuario);
        comprobarUsuarioLogeado(idUsuario);
        tareaService.modificaTarea(idTarea, tarea.getTitulo());
        logger.debug("Tarea modificada correctamente");
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        return "redirect:/usuarios/" + tareaExistente.getUsuarioId() + "/tareas";
    }

    /**
     * Borra una tarea existente. Devuelve una respuesta vacía si tiene éxito.
     */
    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        TareaData tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuarioId());

        tareaService.borraTarea(idTarea);
        return "";
    }
}

