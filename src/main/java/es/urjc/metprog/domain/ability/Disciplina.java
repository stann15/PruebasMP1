package es.urjc.metprog.domain.ability;

import es.urjc.metprog.domain.common.Validaciones;

public class Disciplina extends HabilidadEspecial {
    private final int costeSangre;

    public Disciplina(String nombre, int ataque, int defensa, int costeSangre) {
        super(nombre, ataque, defensa);
        Validaciones.rango(costeSangre, 1, 3, "coste en sangre");
        this.costeSangre = costeSangre;
    }

    public int getCosteSangre() {
        return costeSangre;
    }

    @Override
    public String resumen() {
        return super.resumen() + " costeSangre=" + costeSangre;
    }
}
