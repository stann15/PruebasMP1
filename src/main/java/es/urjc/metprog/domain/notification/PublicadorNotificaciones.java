package es.urjc.metprog.domain.notification;

import java.util.Collection;

public class PublicadorNotificaciones {
    public void notificar(ObservadorNotificacion observador, TipoNotificacion tipo, String mensaje) {
        observador.recibirNotificacion(new EventoNotificacion(tipo, mensaje));
    }

    public void notificarTodos(Collection<? extends ObservadorNotificacion> observadores, TipoNotificacion tipo, String mensaje) {
        EventoNotificacion evento = new EventoNotificacion(tipo, mensaje);
        for (ObservadorNotificacion observador : observadores) {
            observador.recibirNotificacion(evento);
        }
    }
}
