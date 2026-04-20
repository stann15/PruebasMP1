package es.urjc.metprog.domain.notification;

public interface ObservadorNotificacion {
    void recibirNotificacion(EventoNotificacion evento);
}
