package es.urjc.metprog.domain.ranking;

public record EntradaRanking(
        int posicion,
        String nickJugador,
        String nombrePersonaje,
        int oroActual,
        int victorias,
        int derrotas
) {
    @Override
    public String toString() {
        return posicion + ". " + nickJugador + " / " + nombrePersonaje + " - oro=" + oroActual + ", victorias=" + victorias + ", derrotas=" + derrotas;
    }
}
