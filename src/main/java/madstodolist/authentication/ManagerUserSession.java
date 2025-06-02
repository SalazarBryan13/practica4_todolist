package madstodolist.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;

@Component
public class ManagerUserSession {

    @Autowired
<<<<<<< HEAD
    private HttpSession session;
=======
    HttpSession session;
>>>>>>> 9207783e011d3b7383852f076cef692c1c05ebab

    // Añadimos el id de usuario en la sesión HTTP para hacer
    // una autorización sencilla. En los métodos de controllers
    // comprobamos si el id del usuario logeado coincide con el obtenido
    // desde la URL
<<<<<<< HEAD
    public void logearUsuario(Long idUsuario, String nombreUsuario) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
        session.setAttribute("nombreUsuario", nombreUsuario);
=======
    public void logearUsuario(Long idUsuario) {
        session.setAttribute("idUsuarioLogeado", idUsuario);
>>>>>>> 9207783e011d3b7383852f076cef692c1c05ebab
    }

    public Long usuarioLogeado() {
        return (Long) session.getAttribute("idUsuarioLogeado");
    }

<<<<<<< HEAD
    public String nombreUsuarioLogeado() {
        return (String) session.getAttribute("nombreUsuario");
    }

    public void logout() {
        session.setAttribute("idUsuarioLogeado", null);
        session.setAttribute("nombreUsuario", null);
=======
    public void logout() {
        session.setAttribute("idUsuarioLogeado", null);
>>>>>>> 9207783e011d3b7383852f076cef692c1c05ebab
    }
}
