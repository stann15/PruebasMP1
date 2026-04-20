package es.urjc.metprog.tests;

import es.urjc.metprog.app.SistemaFacade;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.persistence.MovimientoOro;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.RolUsuario;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

final class SistemaFacadeFlujosTest {
    private static final Pattern REGISTRO = Pattern.compile("[A-Z]\\d\\d[A-Z][A-Z]");

    private SistemaFacadeFlujosTest() {
    }

    static void registraAutenticaYControlaRoles() throws Exception {
        TestSupport.resetPersistence();
        SistemaFacade sistema = new SistemaFacade();

        Jugador jugador = sistema.registrarJugador("Ana", "ana", "12345678");
        sistema.registrarOperador("Operador", "op", "12345678");

        TestSupport.assertTrue(REGISTRO.matcher(jugador.getNumeroRegistro()).matches(), "El registro de jugador cumple el formato LNNLL.");
        TestSupport.assertDomainException(
                () -> sistema.registrarJugador("Duplicada", "ana", "12345678"),
                "No se permite registrar dos usuarios con el mismo nick."
        );
        TestSupport.assertDomainException(
                () -> sistema.iniciarSesion("ana", "incorrecta"),
                "No se permite iniciar sesion con password incorrecta."
        );

        sistema.iniciarSesion("ana", "12345678");
        TestSupport.assertEquals(RolUsuario.JUGADOR, sistema.getRolActual(), "El rol de la sesion de jugador se conserva.");
        TestSupport.assertDomainException(sistema::listarJugadores, "Un jugador no puede usar operaciones de operador.");
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "12345678");
        TestSupport.assertEquals(1, sistema.listarJugadores().size(), "El operador puede listar jugadores.");
        sistema.cerrarSesion();
    }

    static void desafioValidadoBloqueaAccionesYRechazoPenaliza() throws Exception {
        TestSupport.resetPersistence();
        SistemaFacade sistema = sistemaConDosJugadoresEquipados();

        sistema.iniciarSesion("alice", "12345678");
        Desafio desafio = sistema.lanzarDesafio("bob", 10);
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "12345678");
        sistema.validarDesafio(desafio.getId(), List.of("Noche"), List.of());
        sistema.cerrarSesion();

        sistema.iniciarSesion("bob", "12345678");
        TestSupport.assertDomainException(
                sistema::consultarRanking,
                "Un jugador con desafio pendiente no puede consultar ranking antes de responder."
        );
        sistema.rechazarDesafio(desafio.getId());
        List<MovimientoOro> movimientosBob = sistema.listarMovimientosOroJugadorActual();
        sistema.cerrarSesion();

        TestSupport.assertTrue(
                movimientosBob.stream().anyMatch(movimiento -> movimiento.getDelta() == -1 && movimiento.getConcepto().contains("Penalizacion")),
                "Rechazar un desafio descuenta el 10% redondeado hacia arriba."
        );

        sistema.iniciarSesion("alice", "12345678");
        String notificaciones = String.join("\n", sistema.verNotificacionesActuales());
        TestSupport.assertContains(notificaciones, "DESAFIO_RECHAZADO", "El desafiante recibe notificacion del rechazo.");
        sistema.cerrarSesion();
    }

    static void validacionAplicaReglaDe24HorasYBloqueaDesafiante() throws Exception {
        TestSupport.resetPersistence();
        SistemaFacade sistema = sistemaConDosJugadoresEquipados();
        sistema.buscarJugadorPublico("bob").orElseThrow().registrarDerrota(LocalDateTime.now());

        sistema.iniciarSesion("alice", "12345678");
        Desafio desafio = sistema.lanzarDesafio("bob", 5);
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "12345678");
        sistema.validarDesafio(desafio.getId(), List.of(), List.of());
        sistema.cerrarSesion();

        Jugador alice = sistema.buscarJugadorPublico("alice").orElseThrow();
        TestSupport.assertTrue(alice.isBloqueado(), "El desafiante queda bloqueado si reta a quien perdio en las ultimas 24 horas.");
        TestSupport.assertEquals("RECHAZADO", desafio.getNombreEstado(), "El desafio queda rechazado administrativamente.");
    }

    static void validaApuestaEquipoYPersistenciaBasica() throws Exception {
        TestSupport.resetPersistence();
        SistemaFacade sistema = sistemaConDosJugadoresEquipados();

        sistema.iniciarSesion("alice", "12345678");
        TestSupport.assertDomainException(
                () -> sistema.lanzarDesafio("alice", 1),
                "Un jugador no puede desafiarse a si mismo."
        );
        TestSupport.assertDomainException(
                () -> sistema.lanzarDesafio("bob", -1),
                "La apuesta no puede ser negativa."
        );
        TestSupport.assertDomainException(
                () -> sistema.lanzarDesafio("bob", 999),
                "La apuesta no puede superar el oro disponible."
        );
        sistema.lanzarDesafio("bob", 3);
        TestSupport.assertDomainException(
                () -> sistema.lanzarDesafio("bob", 3),
                "Un usuario con desafio bloqueante no puede abrir otro desafio simultaneo."
        );
        sistema.cerrarSesion();

        TestSupport.assertTrue(Files.exists(Path.of("data", "metprog-combate.dat")), "La persistencia binaria se genera durante las operaciones.");
        TestSupport.assertTrue(Files.exists(Path.of("data", "metprog-combate.txt")), "La copia legible de persistencia se genera durante las operaciones.");
    }

    private static SistemaFacade sistemaConDosJugadoresEquipados() {
        SistemaFacade sistema = new SistemaFacade();
        sistema.registrarOperador("Operador", "op", "12345678");
        sistema.registrarJugador("Alice", "alice", "12345678");
        sistema.registrarJugador("Bob", "bob", "12345678");

        sistema.iniciarSesion("alice", "12345678");
        sistema.crearPersonajeParaJugadorActual(TestFixtures.configVampiro("Vlad", 4, 40, 7));
        sistema.cerrarSesion();

        sistema.iniciarSesion("bob", "12345678");
        sistema.crearPersonajeParaJugadorActual(TestFixtures.configCazador("Van", 4, 40));
        sistema.cerrarSesion();
        return sistema;
    }
}
