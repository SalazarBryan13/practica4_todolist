package madstodolist.service;

import madstodolist.model.Tarea;
import madstodolist.model.PrioridadTarea;
import madstodolist.repository.TareaRepository;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import madstodolist.dto.TareaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TareaService {

    Logger logger = LoggerFactory.getLogger(TareaService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TareaRepository tareaRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private NotificacionService notificacionService;

    @Transactional
    public TareaData nuevaTareaUsuario(Long idUsuario, String tituloTarea) {
        logger.debug("Añadiendo tarea " + tituloTarea + " al usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario, tituloTarea, PrioridadTarea.MEDIA);
        tareaRepository.save(tarea);
        notificacionService.crearNotificacion(tarea, usuario, "Nueva tarea creada: " + tituloTarea);
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public TareaData nuevaTareaUsuario(Long idUsuario, String tituloTarea, PrioridadTarea prioridad) {
        logger.debug("Añadiendo tarea " + tituloTarea + " al usuario " + idUsuario + " con prioridad " + prioridad);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario, tituloTarea, prioridad);
        tareaRepository.save(tarea);
        notificacionService.crearNotificacion(tarea, usuario, "Nueva tarea creada: " + tituloTarea + " (Prioridad: " + prioridad + ")");
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional(readOnly = true)
    public List<TareaData> allTareasUsuario(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<TareaData> tareas = usuario.getTareas().stream()
                .map(tarea -> modelMapper.map(tarea, TareaData.class))
                .collect(Collectors.toList());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return tareas;
    }

    @Transactional(readOnly = true)
    public TareaData findById(Long tareaId) {
        logger.debug("Buscando tarea " + tareaId);
        Tarea tarea = tareaRepository.findById(tareaId).orElse(null);
        if (tarea == null) return null;
        else return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public TareaData modificaTarea(Long idTarea, String nuevoTitulo) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        String antiguoTitulo = tarea.getTitulo();
        tarea.setTitulo(nuevoTitulo);
        tarea = tareaRepository.save(tarea);
        notificacionService.crearNotificacion(tarea, tarea.getUsuario(), 
            "Tarea modificada: " + antiguoTitulo + " → " + nuevoTitulo);
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public TareaData modificaTarea(Long idTarea, String nuevoTitulo, PrioridadTarea nuevaPrioridad) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo + " con prioridad " + nuevaPrioridad);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        String antiguoTitulo = tarea.getTitulo();
        PrioridadTarea antiguaPrioridad = tarea.getPrioridad();
        tarea.setTitulo(nuevoTitulo);
        tarea.setPrioridad(nuevaPrioridad);
        tarea = tareaRepository.save(tarea);
        notificacionService.crearNotificacion(tarea, tarea.getUsuario(),
            "Tarea modificada: " + antiguoTitulo + " → " + nuevoTitulo + 
            " (Prioridad: " + antiguaPrioridad + " → " + nuevaPrioridad + ")");
        return modelMapper.map(tarea, TareaData.class);
    }

    @Transactional
    public void borraTarea(Long idTarea) {
        logger.debug("Borrando tarea " + idTarea);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        Usuario usuario = tarea.getUsuario();
        String tituloTarea = tarea.getTitulo();
        tareaRepository.delete(tarea);
        notificacionService.crearNotificacion(tarea, usuario, "Tarea eliminada: " + tituloTarea);
    }

    @Transactional
    public boolean usuarioContieneTarea(Long usuarioId, Long tareaId) {
        Tarea tarea = tareaRepository.findById(tareaId).orElse(null);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (tarea == null || usuario == null) {
            throw new TareaServiceException("No existe tarea o usuario id");
        }
        return usuario.getTareas().contains(tarea);
    }
}
