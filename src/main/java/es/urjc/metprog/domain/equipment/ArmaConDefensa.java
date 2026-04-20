package es.urjc.metprog.domain.equipment;

import es.urjc.metprog.domain.common.Validaciones;

public class ArmaConDefensa extends EquipoDecorador implements Arma {
    private final Arma arma;
    private final int defensaExtra;

    public ArmaConDefensa(Arma arma, int defensaExtra) {
        super(arma);
        Validaciones.rango(defensaExtra, 1, 3, "defensa extra del arma");
        this.arma = arma;
        this.defensaExtra = defensaExtra;
    }

    @Override
    public int getDefensa() {
        return super.getDefensa() + defensaExtra;
    }

    @Override
    public TipoMano getTipoMano() {
        return arma.getTipoMano();
    }

    @Override
    public String descripcion() {
        return getNombre() + " [arma " + getTipoMano() + ", atk=" + getAtaque() + ", def=" + getDefensa() + "]";
    }
}
