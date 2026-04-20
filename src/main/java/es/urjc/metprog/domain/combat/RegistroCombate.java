package es.urjc.metprog.domain.combat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RegistroCombate implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String nickDesafiante;
    private final String nickDesafiado;
    private final int rondas;
    private final LocalDateTime fecha;
    private final String vencedor;
    private final List<String> esbirrosSupervivientesDesafiante;
    private final List<String> esbirrosSupervivientesDesafiado;
    private final int oroGanado;
    private final List<String> eventos;

    public RegistroCombate(
            String nickDesafiante,
            String nickDesafiado,
            int rondas,
            LocalDateTime fecha,
            String vencedor,
            List<String> esbirrosSupervivientesDesafiante,
            List<String> esbirrosSupervivientesDesafiado,
            int oroGanado,
            List<String> eventos
    ) {
        this.nickDesafiante = nickDesafiante;
        this.nickDesafiado = nickDesafiado;
        this.rondas = rondas;
        this.fecha = fecha;
        this.vencedor = vencedor;
        this.esbirrosSupervivientesDesafiante = new ArrayList<>(esbirrosSupervivientesDesafiante);
        this.esbirrosSupervivientesDesafiado = new ArrayList<>(esbirrosSupervivientesDesafiado);
        this.oroGanado = oroGanado;
        this.eventos = new ArrayList<>(eventos);
    }

    public String getNickDesafiante() {
        return nickDesafiante;
    }

    public String getNickDesafiado() {
        return nickDesafiado;
    }

    public int getRondas() {
        return rondas;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getVencedor() {
        return vencedor;
    }

    public boolean esEmpate() {
        return vencedor == null;
    }

    public List<String> getEsbirrosSupervivientesDesafiante() {
        return Collections.unmodifiableList(esbirrosSupervivientesDesafiante);
    }

    public List<String> getEsbirrosSupervivientesDesafiado() {
        return Collections.unmodifiableList(esbirrosSupervivientesDesafiado);
    }

    public int getOroGanado() {
        return oroGanado;
    }

    public List<String> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

    @Override
    public String toString() {
        return "Combate{" +
                "desafiante='" + nickDesafiante + '\'' +
                ", desafiado='" + nickDesafiado + '\'' +
                ", rondas=" + rondas +
                ", fecha=" + fecha +
                ", vencedor=" + (vencedor == null ? "EMPATE" : vencedor) +
                ", oroGanado=" + oroGanado +
                '}';
    }
}
