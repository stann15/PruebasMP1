package es.urjc.metprog.domain.equipment;

import java.io.Serializable;

public interface Equipo extends Serializable {
    String getNombre();

    int getAtaque();

    int getDefensa();

    String descripcion();
}
