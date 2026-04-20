package es.urjc.metprog.domain.equipment;

import java.io.Serial;

public abstract class EquipoDecorador implements Equipo {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Equipo base;

    protected EquipoDecorador(Equipo base) {
        this.base = base;
    }

    protected Equipo getBase() {
        return base;
    }

    @Override
    public String getNombre() {
        return base.getNombre();
    }

    @Override
    public int getAtaque() {
        return base.getAtaque();
    }

    @Override
    public int getDefensa() {
        return base.getDefensa();
    }
}
