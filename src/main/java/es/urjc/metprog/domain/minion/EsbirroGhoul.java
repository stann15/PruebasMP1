package es.urjc.metprog.domain.minion;

import es.urjc.metprog.domain.common.Validaciones;

public class EsbirroGhoul extends EsbirroBase {
    private final int dependencia;

    public EsbirroGhoul(String nombre, int salud, int dependencia) {
        super(nombre, salud);
        Validaciones.rango(dependencia, 1, 5, "dependencia del ghoul");
        this.dependencia = dependencia;
    }

    public int getDependencia() {
        return dependencia;
    }

    @Override
    public String descripcion() {
        return getNombre() + " [ghoul, salud=" + getSaludPropia() + ", dependencia=" + dependencia + "]";
    }
}
