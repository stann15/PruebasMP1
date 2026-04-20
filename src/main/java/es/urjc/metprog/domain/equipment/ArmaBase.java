package es.urjc.metprog.domain.equipment;

import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;

public class ArmaBase implements Arma {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int ataque;
    private final TipoMano tipoMano;

    public ArmaBase(String nombre, int ataque, TipoMano tipoMano) {
        Validaciones.noVacio(nombre, "nombre del arma");
        Validaciones.rango(ataque, 1, 3, "ataque del arma");
        this.nombre = nombre.trim();
        this.ataque = ataque;
        this.tipoMano = tipoMano;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getAtaque() {
        return ataque;
    }

    @Override
    public int getDefensa() {
        return 0;
    }

    @Override
    public TipoMano getTipoMano() {
        return tipoMano;
    }

    @Override
    public String descripcion() {
        return nombre + " [arma " + tipoMano + ", atk=" + getAtaque() + ", def=" + getDefensa() + "]";
    }
}
