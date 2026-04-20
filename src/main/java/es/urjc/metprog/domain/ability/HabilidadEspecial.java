package es.urjc.metprog.domain.ability;

import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;
import java.io.Serializable;

public abstract class HabilidadEspecial implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int ataque;
    private final int defensa;

    protected HabilidadEspecial(String nombre, int ataque, int defensa) {
        Validaciones.noVacio(nombre, "nombre de la habilidad");
        Validaciones.rango(ataque, 1, 3, "ataque de la habilidad");
        Validaciones.rango(defensa, 1, 3, "defensa de la habilidad");
        this.nombre = nombre.trim();
        this.ataque = ataque;
        this.defensa = defensa;
    }

    public String getNombre() {
        return nombre;
    }

    public int getAtaque() {
        return ataque;
    }

    public int getDefensa() {
        return defensa;
    }

    public String resumen() {
        return nombre + " [ATK=" + ataque + ", DEF=" + defensa + "]";
    }
}
