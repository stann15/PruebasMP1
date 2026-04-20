package es.urjc.metprog.domain.character;

import java.io.Serial;
import java.io.Serializable;

public interface EstadoPersonaje extends Serializable {
    String nombre();

    boolean puedeActuar();

    static EstadoPersonaje normal() {
        return EstadoNormal.INSTANCE;
    }

    static EstadoPersonaje bestia() {
        return EstadoBestia.INSTANCE;
    }

    static EstadoPersonaje muerto() {
        return EstadoMuerto.INSTANCE;
    }
}

final class EstadoNormal implements EstadoPersonaje {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoNormal INSTANCE = new EstadoNormal();

    private EstadoNormal() {
    }

    @Override
    public String nombre() {
        return "NORMAL";
    }

    @Override
    public boolean puedeActuar() {
        return true;
    }
}

final class EstadoBestia implements EstadoPersonaje {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoBestia INSTANCE = new EstadoBestia();

    private EstadoBestia() {
    }

    @Override
    public String nombre() {
        return "BESTIA";
    }

    @Override
    public boolean puedeActuar() {
        return true;
    }
}

final class EstadoMuerto implements EstadoPersonaje {
    @Serial
    private static final long serialVersionUID = 1L;
    static final EstadoMuerto INSTANCE = new EstadoMuerto();

    private EstadoMuerto() {
    }

    @Override
    public String nombre() {
        return "MUERTO";
    }

    @Override
    public boolean puedeActuar() {
        return false;
    }
}
