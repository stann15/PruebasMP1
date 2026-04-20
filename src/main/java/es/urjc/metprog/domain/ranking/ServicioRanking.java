package es.urjc.metprog.domain.ranking;

import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.user.Jugador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ServicioRanking {
    public List<EntradaRanking> calcularRanking(List<Jugador> jugadores, List<RegistroCombate> combates) {
        Map<String, Long> victorias = combates.stream()
                .filter(registro -> !registro.esEmpate())
                .collect(Collectors.groupingBy(RegistroCombate::getVencedor, Collectors.counting()));

        Map<String, Long> derrotas = combates.stream()
                .filter(registro -> !registro.esEmpate())
                .map(registro -> registro.getVencedor().equals(registro.getNickDesafiante()) ? registro.getNickDesafiado() : registro.getNickDesafiante())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Jugador> participantes = jugadores.stream()
                .filter(Jugador::tienePersonaje)
                .sorted(Comparator
                        .comparingInt((Jugador jugador) -> jugador.getPersonaje().getOro())
                        .reversed()
                        .thenComparing(Comparator.comparingLong((Jugador jugador) -> victorias.getOrDefault(jugador.getNick(), 0L)).reversed())
                        .thenComparing(Jugador::getNick))
                .toList();

        List<EntradaRanking> resultado = new ArrayList<>();
        int posicion = 1;
        for (Jugador jugador : participantes) {
            resultado.add(new EntradaRanking(
                    posicion++,
                    jugador.getNick(),
                    jugador.getPersonaje().getNombre(),
                    jugador.getPersonaje().getOro(),
                    victorias.getOrDefault(jugador.getNick(), 0L).intValue(),
                    derrotas.getOrDefault(jugador.getNick(), 0L).intValue()
            ));
        }
        return resultado;
    }
}
