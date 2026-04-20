package es.urjc.metprog.domain.minion;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GrupoEsbirros implements UnidadControlable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nombre;
    private final List<UnidadControlable> integrantes;

    public GrupoEsbirros(String nombre) {
        this.nombre = nombre;
        this.integrantes = new ArrayList<>();
    }

    public void add(UnidadControlable unidad) {
        integrantes.add(unidad);
    }

    public List<UnidadControlable> getIntegrantes() {
        return Collections.unmodifiableList(integrantes);
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public int getSaludTotal() {
        return integrantes.stream().mapToInt(UnidadControlable::getSaludTotal).sum();
    }

    @Override
    public int aplicarDanio(int puntos) {
        int restante = puntos;
        for (UnidadControlable integrante : integrantes) {
            if (restante <= 0) {
                break;
            }
            restante = integrante.aplicarDanio(restante);
        }
        return restante;
    }

    @Override
    public void restaurar() {
        integrantes.forEach(UnidadControlable::restaurar);
    }

    @Override
    public boolean estaDerrotado() {
        return integrantes.stream().allMatch(UnidadControlable::estaDerrotado);
    }

    @Override
    public List<String> listarSupervivientes() {
        List<String> nombres = new ArrayList<>();
        for (UnidadControlable integrante : integrantes) {
            nombres.addAll(integrante.listarSupervivientes());
        }
        return nombres;
    }

    @Override
    public String descripcion() {
        if (integrantes.isEmpty()) {
            return nombre + ": sin esbirros";
        }
        List<String> descripciones = new ArrayList<>();
        for (UnidadControlable integrante : integrantes) {
            descripciones.add(integrante.descripcion());
        }
        return nombre + ": " + String.join(", ", descripciones);
    }
}
