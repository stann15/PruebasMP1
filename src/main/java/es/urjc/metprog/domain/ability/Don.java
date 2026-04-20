package es.urjc.metprog.domain.ability;

import es.urjc.metprog.domain.common.Validaciones;

public class Don extends HabilidadEspecial {
    private final int rabiaMinima;
    private final boolean incrementaRabiaAlUsarse;

    public Don(String nombre, int ataque, int defensa, int rabiaMinima, boolean incrementaRabiaAlUsarse) {
        super(nombre, ataque, defensa);
        Validaciones.rango(rabiaMinima, 0, 3, "rabia minima");
        this.rabiaMinima = rabiaMinima;
        this.incrementaRabiaAlUsarse = incrementaRabiaAlUsarse;
    }

    public int getRabiaMinima() {
        return rabiaMinima;
    }

    public boolean isIncrementaRabiaAlUsarse() {
        return incrementaRabiaAlUsarse;
    }

    @Override
    public String resumen() {
        return super.resumen() + " rabiaMinima=" + rabiaMinima + " incrementaRabia=" + incrementaRabiaAlUsarse;
    }
}
