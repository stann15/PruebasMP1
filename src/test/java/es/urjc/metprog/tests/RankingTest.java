package es.urjc.metprog.tests;

import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.ranking.EntradaRanking;
import es.urjc.metprog.domain.ranking.ServicioRanking;
import es.urjc.metprog.domain.user.Jugador;

import java.time.LocalDateTime;
import java.util.List;

final class RankingTest {
    private RankingTest() {
    }

    static void ordenaPorOroVictoriasYNickYCalculaDerrotas() {
        Jugador alice = jugador("Alice", "alice", 50);
        Jugador bob = jugador("Bob", "bob", 50);
        Jugador carol = jugador("Carol", "carol", 80);
        Jugador sinPersonaje = new Jugador("Sin", "sin", "12345678", "S00IN");

        List<RegistroCombate> combates = List.of(
                combate("bob", "alice", "bob"),
                combate("alice", "carol", "alice")
        );

        List<EntradaRanking> ranking = new ServicioRanking().calcularRanking(
                List.of(alice, bob, carol, sinPersonaje),
                combates
        );

        TestSupport.assertEquals(3, ranking.size(), "El ranking excluye jugadores sin personaje.");
        TestSupport.assertEquals("carol", ranking.get(0).nickJugador(), "El oro es el primer criterio de ordenacion.");
        TestSupport.assertEquals("alice", ranking.get(1).nickJugador(), "A igualdad de oro y victorias se ordena por nick.");
        TestSupport.assertEquals(1, ranking.get(1).victorias(), "Se calculan victorias acumuladas.");
        TestSupport.assertEquals(1, ranking.get(1).derrotas(), "Se calculan derrotas acumuladas.");
        TestSupport.assertEquals("bob", ranking.get(2).nickJugador(), "Bob queda tras Alice por desempate alfabetico.");
    }

    private static Jugador jugador(String nombre, String nick, int oro) {
        Jugador jugador = new Jugador(nombre, nick, "12345678", nick.substring(0, 1).toUpperCase() + "00AA");
        jugador.setPersonaje(TestFixtures.cazadorEquipado(nombre + " Hunter", 3, oro));
        return jugador;
    }

    private static RegistroCombate combate(String desafiante, String desafiado, String vencedor) {
        return new RegistroCombate(
                desafiante,
                desafiado,
                3,
                LocalDateTime.now(),
                vencedor,
                List.of(),
                List.of(),
                10,
                List.of("Combate sintetico para ranking")
        );
    }
}
