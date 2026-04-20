package es.urjc.metprog.domain.persistence;

import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.Operador;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DatosSistema implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Jugador> jugadores;
    private final Map<String, Operador> operadores;
    private final Map<String, Desafio> desafios;
    private final List<RegistroCombate> combates;
    private final List<MovimientoOro> movimientosOro;
    private int secuenciaDesafios;

    public DatosSistema() {
        this.jugadores = new LinkedHashMap<>();
        this.operadores = new LinkedHashMap<>();
        this.desafios = new LinkedHashMap<>();
        this.combates = new ArrayList<>();
        this.movimientosOro = new ArrayList<>();
        this.secuenciaDesafios = 1;
    }

    public Map<String, Jugador> getJugadores() {
        return jugadores;
    }

    public Map<String, Operador> getOperadores() {
        return operadores;
    }

    public Map<String, Desafio> getDesafios() {
        return desafios;
    }

    public List<RegistroCombate> getCombates() {
        return combates;
    }

    public List<MovimientoOro> getMovimientosOro() {
        return movimientosOro;
    }

    public String siguienteIdDesafio() {
        return "DES-" + secuenciaDesafios++;
    }
}
