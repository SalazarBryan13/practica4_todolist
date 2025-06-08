package madstodolist.service;

import madstodolist.dto.UsuarioData;
import madstodolist.model.Usuario;
import madstodolist.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class UsuarioService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario por email: '{}'", email);
        
        if (email == null || email.trim().isEmpty()) {
            logger.error("Email proporcionado es nulo o vacío");
            throw new UsernameNotFoundException("Email no puede ser nulo o vacío");
        }

        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (!usuario.isPresent()) {
            logger.error("Usuario no encontrado con email: {}", email);
            throw new UsernameNotFoundException("Usuario no encontrado con email: " + email);
        }

        logger.debug("Usuario encontrado: {}", email);
        logger.debug("Password del usuario: {}", usuario.get().getPassword());
        
        return new org.springframework.security.core.userdetails.User(
            usuario.get().getEmail(),
            usuario.get().getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        logger.debug("Intentando login para usuario: {}", eMail);
        
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            logger.debug("Usuario no encontrado: {}", eMail);
            return LoginStatus.USER_NOT_FOUND;
        }
        
        logger.debug("Usuario encontrado, verificando contraseña");
        if (!passwordEncoder.matches(password, usuario.get().getPassword())) {
            logger.debug("Contraseña incorrecta para usuario: {}", eMail);
            return LoginStatus.ERROR_PASSWORD;
        }
        
        logger.debug("Login exitoso para usuario: {}", eMail);
        return LoginStatus.LOGIN_OK;
    }

    @Transactional
    public UsuarioData registrar(UsuarioData usuario) {
        logger.debug("Intentando registrar usuario: {}", usuario.getEmail());
        
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent()) {
            logger.error("El usuario {} ya está registrado", usuario.getEmail());
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        }
        
        if (usuario.getEmail() == null) {
            logger.error("El usuario no tiene email");
            throw new UsuarioServiceException("El usuario no tiene email");
        }
        
        if (usuario.getPassword() == null) {
            logger.error("El usuario no tiene password");
            throw new UsuarioServiceException("El usuario no tiene password");
        }

        Usuario usuarioNuevo = modelMapper.map(usuario, Usuario.class);
        usuarioNuevo.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioNuevo.setAdmin(usuario.isAdmin());
        usuarioNuevo = usuarioRepository.save(usuarioNuevo);
        
        logger.debug("Usuario registrado exitosamente: {}", usuario.getEmail());
        UsuarioData usuarioData = modelMapper.map(usuarioNuevo, UsuarioData.class);
        usuarioData.setAdmin(usuarioNuevo.isAdmin());
        return usuarioData;
    }

    @Transactional(readOnly = true)
    public UsuarioData findByEmail(String email) {
        logger.debug("Buscando usuario por email: {}", email);
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            logger.debug("Usuario no encontrado con email: {}", email);
            return null;
        }
        logger.debug("Usuario encontrado: {} admin={}", usuario.getEmail(), usuario.isAdmin());
        UsuarioData usuarioData = modelMapper.map(usuario, UsuarioData.class);
        logger.debug("UsuarioData recuperado: {} admin={}", usuarioData.getEmail(), usuarioData.isAdmin());
        return usuarioData;
    }

    @Transactional(readOnly = true)
    public UsuarioData findById(Long usuarioId) {
        logger.debug("Buscando usuario por id: {}", usuarioId);
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            logger.debug("Usuario no encontrado con id: {}", usuarioId);
            return null;
        }
        logger.debug("Usuario encontrado con id: {} admin={}", usuarioId, usuario.isAdmin());
        UsuarioData usuarioData = modelMapper.map(usuario, UsuarioData.class);
        logger.debug("UsuarioData recuperado con id: {} admin={}", usuarioId, usuarioData.isAdmin());
        return usuarioData;
    }

    @Transactional(readOnly = true)
    public List<UsuarioData> findAllUsuarios() {
        List<UsuarioData> usuarios = new ArrayList<>();
        for (Usuario usuario : usuarioRepository.findAll()) {
            usuarios.add(modelMapper.map(usuario, UsuarioData.class));
        }
        return usuarios;
    }

    @Transactional(readOnly = true)
    public boolean existeAdmin() {
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (usuario.isAdmin()) {
                return true;
            }
        }
        return false;
    }

    @Transactional
    public void toggleBloqueo(Long id, boolean bloqueado) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        usuario.setBloqueado(bloqueado);
        usuarioRepository.save(usuario);
    }
}
