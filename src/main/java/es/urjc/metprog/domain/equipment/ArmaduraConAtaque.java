package es.urjc.metprog.domain.equipment;

import es.urjc.metprog.domain.common.Validaciones;

public class ArmaduraConAtaque extends EquipoDecorador implements Armadura {
    private final int ataqueExtra;

    public ArmaduraConAtaque(Armadura armadura, int ataqueExtra) {
        super(armadura);
        Validaciones.rango(ataqueExtra, 1, 3, "ataque extra de la armadura");
        this.ataqueExtra = ataqueExtra;
    }

    @Override
    public int getAtaque() {
        return super.getAtaque() + ataqueExtra;
    }

    @Override
    public String descripcion() {
        return getNombre() + " [armadura, atk=" + getAtaque() + ", def=" + getDefensa() + "]";
    }
}
