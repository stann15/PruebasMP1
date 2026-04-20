package es.urjc.metprog.domain.equipment;

import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;

public class ArmaduraBase implements Armadura {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int defensa;

    public ArmaduraBase(String nombre, int defensa) {
        Validaciones.noVacio(nombre, "nombre de la armadura");
        Validaciones.rango(defensa, 1, 3, "defensa de la armadura");
        this.nombre = nombre.trim();
        this.defensa = defensa;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getAtaque() {
        return 0;
    }

    @Override
    public int getDefensa() {
        return defensa;
    }

    @Override
    public String descripcion() {
        return nombre + " [armadura, atk=" + getAtaque() + ", def=" + getDefensa() + "]";
    }
}
