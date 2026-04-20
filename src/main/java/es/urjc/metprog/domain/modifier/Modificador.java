package es.urjc.metprog.domain.modifier;

import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;
import java.io.Serializable;

public class Modificador implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int valor;
    private final TipoModificador tipo;

    public Modificador(String nombre, int valor, TipoModificador tipo) {
        Validaciones.noVacio(nombre, "nombre del modificador");
        Validaciones.rango(valor, 1, 5, "valor del modificador");
        this.nombre = nombre.trim();
        this.valor = valor;
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getValor() {
        return valor;
    }

    public TipoModificador getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return nombre + " (" + tipo + ", " + valor + ")";
    }
}
