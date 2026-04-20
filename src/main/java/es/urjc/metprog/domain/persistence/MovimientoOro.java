package es.urjc.metprog.domain.persistence;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class MovimientoOro implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final LocalDateTime fecha;
    private final String nickJugador;
    private final String concepto;
    private final int delta;
    private final int saldoResultante;

    public MovimientoOro(String nickJugador, String concepto, int delta, int saldoResultante) {
        this.fecha = LocalDateTime.now();
        this.nickJugador = nickJugador;
        this.concepto = concepto;
        this.delta = delta;
        this.saldoResultante = saldoResultante;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getNickJugador() {
        return nickJugador;
    }

    public String getConcepto() {
        return concepto;
    }

    public int getDelta() {
        return delta;
    }

    public int getSaldoResultante() {
        return saldoResultante;
    }

    @Override
    public String toString() {
        return "[" + fecha + "] " + concepto + " -> delta=" + delta + ", saldo=" + saldoResultante;
    }
}
