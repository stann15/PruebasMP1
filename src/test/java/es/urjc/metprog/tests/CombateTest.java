package es.urjc.metprog.tests;

import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.combat.Combate;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.user.Jugador;

import java.util.List;

final class CombateTest {
    private CombateTest() {
    }

    static void combateGeneraRondasEventosYResultadoCoherente() {
        Jugador alice = new Jugador("Alice", "alice", "12345678", "A00AA");
        Jugador bob = new Jugador("Bob", "bob", "12345678", "B00BB");
        Personaje vampiro = TestFixtures.vampiroEquipado("Vlad", 4, 40, 7);
        Personaje cazador = TestFixtures.cazadorEquipado("Van", 4, 40);
        alice.setPersonaje(vampiro);
        bob.setPersonaje(cazador);

        Desafio desafio = new Desafio("DES-C1", "alice", "bob", 5);
        desafio.validar("op", List.of("Noche"), List.of());
        desafio.aceptar();

        RegistroCombate registro = new Combate(desafio, alice, bob).ejecutar();

        TestSupport.assertTrue(registro.getRondas() > 0, "El combate debe ejecutar al menos una ronda.");
        TestSupport.assertTrue(registro.getRondas() <= 500, "El combate no debe superar el limite de seguridad.");
        TestSupport.assertNotEmpty(registro.getEventos(), "El combate debe registrar eventos de las rondas.");
        TestSupport.assertTrue(
                registro.getEventos().stream().anyMatch(evento -> evento.contains("Ronda")),
                "El historial del combate debe contener informacion por ronda."
        );
        TestSupport.assertEquals(registro.esEmpate() ? 0 : 5, registro.getOroGanado(), "El oro ganado coincide con apuesta salvo empate.");
    }
}
