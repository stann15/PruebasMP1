package es.urjc.metprog.tests;

import es.urjc.metprog.domain.ability.Talento;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.user.Jugador;

final class ValidacionesTest {
    private ValidacionesTest() {
    }

    static void validaCamposObligatoriosYRangos() {
        TestSupport.assertDomainException(
                () -> new Talento(" ", 1, 1),
                "Una habilidad sin nombre debe rechazarse."
        );
        TestSupport.assertDomainException(
                () -> new Talento("Vision", 0, 1),
                "El ataque de una habilidad debe estar entre 1 y 3."
        );
        TestSupport.assertDomainException(
                () -> new ArmaBase("Espada imposible", 4, TipoMano.UNA_MANO),
                "El ataque de un arma debe estar entre 1 y 3."
        );
    }

    static void jugadorValidaPasswordYRegistro() {
        TestSupport.assertDomainException(
                () -> new Jugador("Ana", "ana", "1234567", "A00BC"),
                "Un jugador con password menor que 8 caracteres debe rechazarse."
        );
        Jugador jugador = new Jugador("Ana", "ana", "12345678", "A00BC");
        TestSupport.assertEquals("A00BC", jugador.getNumeroRegistro(), "El numero de registro se conserva en el jugador.");
        TestSupport.assertTrue(jugador.passwordCorrecto("12345678"), "La password correcta debe autenticar al usuario.");
    }

    static void oroNuncaPuedeSerNegativo() {
        Personaje personaje = TestFixtures.cazadorEquipado("Saldo", 3, 5);
        TestSupport.assertDomainException(
                () -> personaje.ajustarOro(-6),
                "El oro de un personaje no puede quedar por debajo de cero."
        );
    }
}
