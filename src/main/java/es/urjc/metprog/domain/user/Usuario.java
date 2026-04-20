package es.urjc.metprog.domain.user;

import es.urjc.metprog.domain.common.Validaciones;
import es.urjc.metprog.domain.notification.EventoNotificacion;
import es.urjc.metprog.domain.notification.ObservadorNotificacion;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Usuario implements Serializable, ObservadorNotificacion {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final String nick;
    private final String password;
    private final RolUsuario rol;
    private boolean sesionActiva;
    private boolean bloqueado;
    private String motivoBloqueo;
    private final List<EventoNotificacion> bandeja;

    protected Usuario(String nombre, String nick, String password, RolUsuario rol) {
        Validaciones.noVacio(nombre, "nombre");
        Validaciones.noVacio(nick, "nick");
        Validaciones.noVacio(password, "password");
        this.nombre = nombre.trim();
        this.nick = nick.trim();
        this.password = password;
        this.rol = rol;
        this.bandeja = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public String getNick() {
        return nick;
    }

    public boolean passwordCorrecto(String intento) {
        return password.equals(intento);
    }

    public RolUsuario getRol() {
        return rol;
    }

    public boolean isSesionActiva() {
        return sesionActiva;
    }

    public void iniciarSesion() {
        this.sesionActiva = true;
    }

    public void cerrarSesion() {
        this.sesionActiva = false;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }

    public String getMotivoBloqueo() {
        return motivoBloqueo;
    }

    public void bloquear(String motivo) {
        this.bloqueado = true;
        this.motivoBloqueo = motivo;
    }

    public void desbloquear() {
        this.bloqueado = false;
        this.motivoBloqueo = null;
    }

    public List<EventoNotificacion> getBandeja() {
        return Collections.unmodifiableList(bandeja);
    }

    @Override
    public void recibirNotificacion(EventoNotificacion evento) {
        bandeja.add(evento);
    }

    public void limpiarNotificaciones() {
        bandeja.clear();
    }

    @Override
    public String toString() {
        return rol + " " + nick + " (" + nombre + ")";
    }
}
