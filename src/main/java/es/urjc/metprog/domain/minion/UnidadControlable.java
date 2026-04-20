package es.urjc.metprog.domain.minion;

import java.io.Serializable;
import java.util.List;

public interface UnidadControlable extends Serializable {
    String getNombre();

    int getSaludTotal();

    int aplicarDanio(int puntos);

    void restaurar();

    boolean estaDerrotado();

    List<String> listarSupervivientes();

    String descripcion();
}
