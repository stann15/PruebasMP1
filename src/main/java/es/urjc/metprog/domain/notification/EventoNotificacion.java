package es.urjc.metprog.domain.notification;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class EventoNotificacion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDateTime fecha;
    private final TipoNotificacion tipo;
    private final String mensaje;

    public EventoNotificacion(TipoNotificacion tipo, String mensaje) {
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + tipo + ": " + mensaje;
    }
}
