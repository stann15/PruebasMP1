package es.urjc.metprog.domain.ranking;

import es.urjc.metprog.domain.character.TipoPersonaje;

public record EntradaRanking(
        int posicion,
        String nickJugador,
        String nombrePersonaje,
        TipoPersonaje tipoPersonaje,
        int oroActual,
        int victorias,
        int derrotas
) {
    @Override
    public String toString() {
        return posicion + ". " + nickJugador + " / " + nombrePersonaje + " (" + tipoPersonaje + ") - oro=" + oroActual + ", victorias=" + victorias + ", derrotas=" + derrotas;
    }
}
