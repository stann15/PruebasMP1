package es.urjc.metprog.verification;

import es.urjc.metprog.app.SistemaFacade;
import es.urjc.metprog.domain.ability.Disciplina;
import es.urjc.metprog.domain.ability.Don;
import es.urjc.metprog.domain.ability.Talento;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.Cazador;
import es.urjc.metprog.domain.character.ConfiguracionPersonaje;
import es.urjc.metprog.domain.character.Licantropo;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.character.TipoPersonaje;
import es.urjc.metprog.domain.character.Vampiro;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.ArmaduraBase;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.EsbirroDemonio;
import es.urjc.metprog.domain.minion.EsbirroHumano;
import es.urjc.metprog.domain.minion.EsbirroGhoul;
import es.urjc.metprog.domain.minion.Lealtad;
import es.urjc.metprog.domain.persistence.MovimientoOro;
import es.urjc.metprog.domain.ranking.EntradaRanking;
import es.urjc.metprog.domain.user.Jugador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

public class VerificationRunner {
    private static final Path DATA_FILE = Path.of("data", "metprog-combate.dat");
    private static final Pattern REGISTRO_PATTERN = Pattern.compile("[A-Z]\\d\\d[A-Z][A-Z]");

    public static void main(String[] args) throws Exception {
        String mode = args.length == 0 ? "fresh" : args[0];
        if ("fresh".equalsIgnoreCase(mode)) {
            Files.deleteIfExists(DATA_FILE);
            runFreshVerification();
            return;
        }
        if ("persistence-check".equalsIgnoreCase(mode)) {
            runPersistenceVerification();
            return;
        }
        throw new IllegalArgumentException("Modo no soportado: " + mode);
    }

    private static void runFreshVerification() throws Exception {
        SistemaFacade sistema = new SistemaFacade();

        log("Registro de usuarios");
        Jugador alice = sistema.registrarJugador("Alice", "alice", "clave123");
        Jugador bob = sistema.registrarJugador("Bob", "bob", "clave234");
        Jugador carol = sistema.registrarJugador("Carol", "carol", "clave345");
        Jugador dave = sistema.registrarJugador("Dave", "dave", "clave456");
        sistema.registrarJugador("Erin", "erin", "clave567");
        sistema.registrarOperador("Operador", "op", "admin1234");
        assertTrue(REGISTRO_PATTERN.matcher(alice.getNumeroRegistro()).matches(), "Formato de registro valido para Alice");
        assertTrue(REGISTRO_PATTERN.matcher(bob.getNumeroRegistro()).matches(), "Formato de registro valido para Bob");
        assertTrue(!alice.getNumeroRegistro().equals(bob.getNumeroRegistro()), "Registros de jugador unicos");
        assertThrows(() -> sistema.registrarJugador("Otro", "alice", "clave567"), "No se permite duplicar nick");
        assertThrows(() -> sistema.registrarJugador("Corto", "short", "123"), "Se valida longitud minima de password");

        log("Autenticacion basica");
        assertThrows(() -> sistema.iniciarSesion("alice", "incorrecta"), "No se puede iniciar sesion con password incorrecta");
        sistema.iniciarSesion("alice", "clave123");
        assertTrue(sistema.haySesionActiva(), "Sesion de Alice activa");
        sistema.cerrarSesion();
        assertTrue(!sistema.haySesionActiva(), "Sesion cerrada correctamente");

        log("Creacion de personajes");
        sistema.iniciarSesion("alice", "clave123");
        sistema.crearPersonajeParaJugadorActual(crearVampiro("Dracula"));
        sistema.cerrarSesion();

        sistema.iniciarSesion("bob", "clave234");
        sistema.crearPersonajeParaJugadorActual(crearCazador("VanHelsing"));
        assertThrows(
                () -> sistema.equiparJugadorActual(List.of("Rifle Largo", "Pistola"), "Gabardina"),
                "No se puede equipar arma de dos manos junto a otra arma"
        );
        sistema.equiparJugadorActual(List.of("Rifle Largo"), "Gabardina");
        sistema.cerrarSesion();

        sistema.iniciarSesion("carol", "clave345");
        sistema.crearPersonajeParaJugadorActual(crearLicantropo("Fenrir"));
        sistema.cerrarSesion();

        sistema.iniciarSesion("dave", "clave456");
        sistema.crearPersonajeParaJugadorActual(crearCazadorLigero("Artemis"));
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "admin1234");
        assertThrows(
                () -> sistema.addEsbirroAJugador("alice", new EsbirroHumano("HumanoProhibido", 2, Lealtad.ALTA)),
                "Un vampiro no puede recibir esbirros humanos"
        );
        sistema.addEsbirroAJugador("alice", new EsbirroGhoul("GhoulValido", 2, 3));
        EsbirroDemonio demonio = new EsbirroDemonio("Baal", 2, "Pacto ancestral");
        demonio.addSubordinado(new EsbirroGhoul("SubGhoul", 1, 2));
        sistema.addEsbirroAJugador("carol", demonio);
        Personaje daveEditado = sistema.reemplazarPersonajeJugador("dave", crearCazadorLigero("Artemis Prime"));
        sistema.cerrarSesion();
        assertTrue("Artemis Prime".equals(daveEditado.getNombre()), "El operador puede reemplazar completamente el personaje de un jugador");

        log("Mecanicas internas del dominio");
        Vampiro vampiro = (Vampiro) sistema.buscarJugadorPublico("alice").orElseThrow().getPersonaje();
        assertTrue(vampiro.getSaludTotalEsbirros() == 2, "La salud total de esbirros del vampiro se calcula correctamente");
        vampiro.aplicarDanio(1);
        assertTrue(vampiro.getSaludActual() == 5, "El dano se descuenta primero de los esbirros antes que del personaje");
        assertTrue(vampiro.getSaludTotalEsbirros() == 1, "La salud acumulada de esbirros disminuye al recibir dano");
        vampiro.prepararParaCombate();
        assertTrue(vampiro.getSaludTotalEsbirros() == 2, "Los esbirros se restauran al preparar un combate");

        Licantropo licantropo = (Licantropo) sistema.buscarJugadorPublico("carol").orElseThrow().getPersonaje();
        assertTrue(licantropo.getSaludTotalEsbirros() == 3, "La salud recursiva de demonios y subordinados se suma correctamente");
        licantropo.prepararParaCombate();
        licantropo.aplicarDanio(4);
        assertTrue(licantropo.getRabiaActual() == 1, "La rabia del licantropo aumenta al recibir dano");

        Cazador cazador = (Cazador) sistema.buscarJugadorPublico("bob").orElseThrow().getPersonaje();
        cazador.prepararParaCombate();
        cazador.aplicarDanio(1);
        assertTrue(cazador.getVoluntadActual() == 2, "La voluntad del cazador disminuye al recibir dano");

        log("Baja de personaje y cuenta");
        sistema.iniciarSesion("erin", "clave567");
        sistema.crearPersonajeParaJugadorActual(crearCazadorLigero("Temporal"));
        sistema.eliminarPersonajeActual();
        assertTrue(!sistema.buscarJugadorPublico("erin").orElseThrow().tienePersonaje(), "Un jugador puede dar de baja su personaje");
        sistema.darBajaCuentaActual();
        assertTrue(sistema.buscarJugadorPublico("erin").isEmpty(), "Un jugador puede darse de baja del sistema");

        log("Desafio con aceptacion y combate");
        RegistroCombate combatePrincipal = null;
        String loser = null;
        for (int intento = 1; intento <= 6 && loser == null; intento++) {
            sistema.iniciarSesion("alice", "clave123");
            Desafio desafio = sistema.lanzarDesafio("bob", 5);
            sistema.cerrarSesion();

            sistema.iniciarSesion("op", "admin1234");
            sistema.validarDesafio(desafio.getId(), List.of("Noche"), List.of());
            sistema.cerrarSesion();

            sistema.iniciarSesion("bob", "clave234");
            combatePrincipal = sistema.aceptarDesafio(desafio.getId());
            sistema.cerrarSesion();

            assertTrue(combatePrincipal.getRondas() > 0, "El combate registra rondas");
            assertTrue(!combatePrincipal.getEventos().isEmpty(), "El combate registra eventos por ronda");
            if (!combatePrincipal.esEmpate()) {
                loser = combatePrincipal.getVencedor().equals("alice") ? "bob" : "alice";
            }
        }
        assertTrue(combatePrincipal != null, "Se ejecuto al menos un combate");
        assertTrue(loser != null, "Se obtuvo un combate con vencedor para probar la regla de las 24 horas");

        sistema.iniciarSesion("alice", "clave123");
        List<MovimientoOro> movimientosAlice = sistema.listarMovimientosOroJugadorActual();
        List<EntradaRanking> ranking = sistema.consultarRanking();
        sistema.cerrarSesion();
        assertTrue(!movimientosAlice.isEmpty(), "Se registran movimientos de oro tras los combates");
        assertTrue(!ranking.isEmpty(), "El ranking global devuelve entradas");

        sistema.iniciarSesion("alice", "clave123");
        List<String> notificacionesAlice = sistema.verNotificacionesActuales();
        sistema.cerrarSesion();
        assertTrue(notificacionesAlice.stream().anyMatch(item -> item.contains("RESULTADO_COMBATE")), "El desafiante recibe notificacion del resultado");

        log("Desafio con rechazo y penalizacion");
        sistema.iniciarSesion("carol", "clave345");
        Desafio desafioRechazo = sistema.lanzarDesafio("dave", 10);
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "admin1234");
        sistema.validarDesafio(desafioRechazo.getId(), List.of(), List.of());
        sistema.cerrarSesion();

        sistema.iniciarSesion("dave", "clave456");
        assertThrows(() -> sistema.consultarRanking(), "Un jugador con desafio pendiente no puede ejecutar otras acciones");
        sistema.rechazarDesafio(desafioRechazo.getId());
        List<MovimientoOro> movimientosDave = sistema.listarMovimientosOroJugadorActual();
        sistema.cerrarSesion();
        assertTrue(movimientosDave.stream().anyMatch(item -> item.getConcepto().contains("Penalizacion")), "El rechazo genera penalizacion economica");

        log("Regla de las 24 horas");
        String challenger = loser.equals("bob") ? "dave" : "bob";
        sistema.iniciarSesion(challenger, challenger.equals("dave") ? "clave456" : "clave234");
        Desafio desafioIlicito = sistema.lanzarDesafio(loser, 3);
        sistema.cerrarSesion();

        sistema.iniciarSesion("op", "admin1234");
        sistema.validarDesafio(desafioIlicito.getId(), List.of(), List.of());
        sistema.cerrarSesion();

        sistema.iniciarSesion(challenger, challenger.equals("dave") ? "clave456" : "clave234");
        Jugador jugadorBloqueado = sistema.buscarJugadorPublico(challenger).orElseThrow();
        sistema.cerrarSesion();
        assertTrue(jugadorBloqueado.isBloqueado(), "El desafiante ilicito queda bloqueado por la regla de las 24 horas");

        sistema.iniciarSesion("op", "admin1234");
        sistema.desbloquearJugador(challenger);
        sistema.cerrarSesion();
        assertTrue(!sistema.buscarJugadorPublico(challenger).orElseThrow().isBloqueado(), "El operador puede desbloquear usuarios");

        assertTrue(Files.exists(DATA_FILE), "Se genera fichero de persistencia");
        assertTrue(Files.size(DATA_FILE) > 0, "El fichero de persistencia no esta vacio");
        log("Verificacion fresh completada");
    }

    private static void runPersistenceVerification() {
        assertTrue(Files.exists(DATA_FILE), "El fichero persistido existe antes de arrancar");
        SistemaFacade sistema = new SistemaFacade();
        sistema.iniciarSesion("op", "admin1234");
        List<Jugador> jugadores = sistema.listarJugadores();
        sistema.cerrarSesion();
        assertTrue(jugadores.size() >= 4, "Tras reiniciar se recuperan los jugadores persistidos");
        assertTrue(sistema.buscarJugadorPublico("alice").isPresent(), "Alice sigue persistida tras reinicio");
        assertTrue(sistema.buscarJugadorPublico("alice").orElseThrow().tienePersonaje(), "El personaje de Alice sigue persistido");

        sistema.iniciarSesion("alice", "clave123");
        List<MovimientoOro> movimientos = sistema.listarMovimientosOroJugadorActual();
        sistema.cerrarSesion();
        assertTrue(!movimientos.isEmpty(), "Los movimientos de oro siguen disponibles tras reinicio");
        log("Verificacion de persistencia completada");
    }

    private static ConfiguracionPersonaje crearVampiro(String nombre) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.VAMPIRO);
        cfg.setNombre(nombre);
        cfg.setPoder(4);
        cfg.setOro(40);
        cfg.setEdadVampiro(250);
        cfg.setSangreInicial(8);
        cfg.setHabilidadEspecial(new Disciplina("Presencia", 3, 2, 2));
        cfg.getFortalezas().add(new es.urjc.metprog.domain.modifier.Modificador("Noche", 2, es.urjc.metprog.domain.modifier.TipoModificador.FORTALEZA));
        cfg.getDebilidades().add(new es.urjc.metprog.domain.modifier.Modificador("Luz", 2, es.urjc.metprog.domain.modifier.TipoModificador.DEBILIDAD));
        cfg.getArmas().add(new ArmaBase("Daga Ritual", 2, TipoMano.UNA_MANO));
        cfg.getArmas().add(new ArmaBase("Pistola", 1, TipoMano.UNA_MANO));
        cfg.getArmaduras().add(new ArmaduraBase("Capa", 2));
        cfg.getNombresArmasActivas().add("Daga Ritual");
        cfg.getNombresArmasActivas().add("Pistola");
        cfg.setNombreArmaduraActiva("Capa");
        return cfg;
    }

    private static ConfiguracionPersonaje crearCazador(String nombre) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.CAZADOR);
        cfg.setNombre(nombre);
        cfg.setPoder(5);
        cfg.setOro(45);
        cfg.setHabilidadEspecial(new Talento("Fe Inquebrantable", 2, 2));
        cfg.getArmas().add(new ArmaBase("Rifle Largo", 3, TipoMano.DOS_MANOS));
        cfg.getArmas().add(new ArmaBase("Pistola", 1, TipoMano.UNA_MANO));
        cfg.getArmaduras().add(new ArmaduraBase("Gabardina", 2));
        cfg.getNombresArmasActivas().add("Rifle Largo");
        cfg.setNombreArmaduraActiva("Gabardina");
        return cfg;
    }

    private static ConfiguracionPersonaje crearCazadorLigero(String nombre) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.CAZADOR);
        cfg.setNombre(nombre);
        cfg.setPoder(3);
        cfg.setOro(30);
        cfg.setHabilidadEspecial(new Talento("Reflejos", 2, 1));
        cfg.getArmas().add(new ArmaBase("Ballesta", 2, TipoMano.DOS_MANOS));
        cfg.getArmaduras().add(new ArmaduraBase("Cuero", 1));
        cfg.getNombresArmasActivas().add("Ballesta");
        cfg.setNombreArmaduraActiva("Cuero");
        return cfg;
    }

    private static ConfiguracionPersonaje crearLicantropo(String nombre) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.LICANTROPO);
        cfg.setNombre(nombre);
        cfg.setPoder(4);
        cfg.setOro(35);
        cfg.setIncrementoAltura(0.8);
        cfg.setIncrementoPeso(100);
        cfg.setHabilidadEspecial(new Don("Garra Primigenia", 2, 1, 0, true));
        cfg.getArmas().add(new ArmaBase("Garras de Plata", 2, TipoMano.UNA_MANO));
        cfg.getArmas().add(new ArmaBase("Machete", 2, TipoMano.UNA_MANO));
        cfg.getArmaduras().add(new ArmaduraBase("Piel Endurecida", 1));
        cfg.getNombresArmasActivas().add("Garras de Plata");
        cfg.getNombresArmasActivas().add("Machete");
        cfg.setNombreArmaduraActiva("Piel Endurecida");
        return cfg;
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("[OK] " + message);
    }

    private static void assertThrows(CheckedRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (DomainException ex) {
            System.out.println("[OK] " + message + " -> " + ex.getMessage());
            return;
        } catch (Exception ex) {
            throw new AssertionError("Se lanzo una excepcion distinta a la esperada: " + ex.getClass().getSimpleName(), ex);
        }
        throw new AssertionError(message + " (no se lanzo la excepcion esperada)");
    }

    private static void log(String text) {
        System.out.println();
        System.out.println("=== " + text + " ===");
    }

    @FunctionalInterface
    private interface CheckedRunnable {
        void run() throws Exception;
    }
}
