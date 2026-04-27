package es.urjc.metprog.domain.challenge;

import es.urjc.metprog.domain.common.DomainException;

import java.io.Serial;
import java.io.Serializable;

public interface EstadoDesafio extends Serializable{
    String nombre();

    default void validar(Desafio desafio) {
        throw new DomainException("No se puede validar un desafio en estado " + nombre() + ".");
    }

    default void aceptar(Desafio desafio) {
        throw new DomainException("No se puede aceptar un desafio en estado " + nombre() + ".");
    }

    default void rechazar(Desafio desafio) {
        throw new DomainException("No se puede rechazar un desafio en estado " + nombre() + ".");
    }

    default void resolver(Desafio desafio) {
        throw new DomainException("No se puede resolver un desafio en estado " + nombre() + ".");
    }

    static EstadoDesafio pendiente() {
        return EstadoPendiente.INSTANCE;
    }

    static EstadoDesafio validado() {
        return EstadoValidado.INSTANCE;
    }

    static EstadoDesafio aceptado() {
        return EstadoAceptado.INSTANCE;
    }

    static EstadoDesafio rechazado() {
        return EstadoRechazado.INSTANCE;
    }

    static EstadoDesafio resuelto() {
        return EstadoResuelto.INSTANCE;
    }
}

final class EstadoPendiente implements EstadoDesafio {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoPendiente INSTANCE = new EstadoPendiente();

    @Override
    public String nombre() {
        return "PENDIENTE";
    }

    @Override
    public void validar(Desafio desafio) {
    }
}

final class EstadoValidado implements EstadoDesafio {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoValidado INSTANCE = new EstadoValidado();

    @Override
    public String nombre() {
        return "VALIDADO";
    }

    @Override
    public void aceptar(Desafio desafio) {
    }

    @Override
    public void rechazar(Desafio desafio) {
    }
}

final class EstadoAceptado implements EstadoDesafio {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoAceptado INSTANCE = new EstadoAceptado();

    @Override
    public String nombre() {
        return "ACEPTADO";
    }

    @Override
    public void resolver(Desafio desafio) {
    }
}

final class EstadoRechazado implements EstadoDesafio {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoRechazado INSTANCE = new EstadoRechazado();

    @Override
    public String nombre() {
        return "RECHAZADO";
    }
}

final class EstadoResuelto implements EstadoDesafio {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoResuelto INSTANCE = new EstadoResuelto();

    @Override
    public String nombre() {
        return "RESUELTO";
    }
}
