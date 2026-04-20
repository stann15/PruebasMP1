package es.urjc.metprog.domain.minion;

import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;
import java.util.List;

public abstract class EsbirroBase implements UnidadControlable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final int saludMaxima;
    private int saludActual;

    protected EsbirroBase(String nombre, int salud) {
        Validaciones.noVacio(nombre, "nombre del esbirro");
        Validaciones.rango(salud, 1, 3, "salud del esbirro");
        this.nombre = nombre.trim();
        this.saludMaxima = salud;
        this.saludActual = salud;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    protected int getSaludPropia() {
        return saludActual;
    }

    @Override
    public int getSaludTotal() {
        return saludActual;
    }

    @Override
    public int aplicarDanio(int puntos) {
        int restante = puntos;
        while (restante > 0 && saludActual > 0) {
            saludActual--;
            restante--;
        }
        return restante;
    }

    @Override
    public void restaurar() {
        saludActual = saludMaxima;
    }

    @Override
    public boolean estaDerrotado() {
        return saludActual <= 0;
    }

    @Override
    public List<String> listarSupervivientes() {
        return estaDerrotado() ? List.of() : List.of(nombre);
    }
}
