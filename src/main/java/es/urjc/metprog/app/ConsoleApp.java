package es.urjc.metprog.app;

import es.urjc.metprog.domain.ability.Disciplina;
import es.urjc.metprog.domain.ability.Don;
import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.ability.Talento;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.ConfiguracionPersonaje;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.character.TipoPersonaje;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.ArmaConDefensa;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.equipment.ArmaduraBase;
import es.urjc.metprog.domain.equipment.ArmaduraConAtaque;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.EsbirroDemonio;
import es.urjc.metprog.domain.minion.EsbirroGhoul;
import es.urjc.metprog.domain.minion.EsbirroHumano;
import es.urjc.metprog.domain.minion.Lealtad;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;
import es.urjc.metprog.domain.persistence.MovimientoOro;
import es.urjc.metprog.domain.ranking.EntradaRanking;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.Operador;
import es.urjc.metprog.domain.user.Usuario;

import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final SistemaFacade sistema;
    private final Scanner scanner;
    private final InputUtils input;

    public ConsoleApp() {
        this.sistema = new SistemaFacade();
        this.scanner = new Scanner(System.in);
        this.input = new InputUtils(scanner);
    }

    public void ejecutar() {
        boolean salir = false;
        while (!salir) {
            try {
                if (!sistema.haySesionActiva()) {
                    salir = menuPublico();
                } else if (sistema.getUsuarioActual() instanceof Jugador) {
                    menuJugador();
                } else {
                    menuOperador();
                }
            } catch (DomainException e) {
                System.out.println("Error de dominio: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Se ha producido un error inesperado: " + e.getMessage());
            }
            System.out.println();
        }
        System.out.println("Aplicacion finalizada.");
    }

    private boolean menuPublico() {
        System.out.println("=== METPROG COMBATE FANTASTICO ===");
        System.out.println("1. Registrar jugador");
        System.out.println("2. Registrar operador");
        System.out.println("3. Iniciar sesion");
        System.out.println("4. Salir");
        int opcion = input.leerEnteroRango("Selecciona una opcion: ", 1, 4);
        switch (opcion) {
            case 1 -> registrarJugador();
            case 2 -> registrarOperador();
            case 3 -> iniciarSesion();
            case 4 -> {
                return true;
            }
            default -> {
            }
        }
        return false;
    }

    private void registrarJugador() {
        String nombre = input.leerNoVacio("Nombre: ");
        String nick = input.leerNoVacio("Nick: ");
        String password = input.leerNoVacio("Password (8-12 caracteres): ");
        Jugador jugador = sistema.registrarJugador(nombre, nick, password);
        System.out.println("Jugador registrado correctamente. Numero de registro: " + jugador.getNumeroRegistro());
    }

    private void registrarOperador() {
        String nombre = input.leerNoVacio("Nombre: ");
        String nick = input.leerNoVacio("Nick: ");
        String password = input.leerNoVacio("Password: ");
        Operador operador = sistema.registrarOperador(nombre, nick, password);
        System.out.println("Operador registrado correctamente: " + operador.getNick());
    }

    private void iniciarSesion() {
        String nick = input.leerNoVacio("Nick: ");
        String password = input.leerNoVacio("Password: ");
        Usuario usuario = sistema.iniciarSesion(nick, password);
        System.out.println("Sesion iniciada como " + usuario.getRol() + " (" + usuario.getNick() + ").");
        if (usuario.isBloqueado()) {
            System.out.println("Aviso: esta cuenta esta bloqueada. Motivo: " + usuario.getMotivoBloqueo());
        }
    }

    private void menuJugador() {
        Jugador jugador = (Jugador) sistema.getUsuarioActual();
        procesarDesafiosPendientes();
        if (!sistema.haySesionActiva()) {
            return;
        }
        if (jugador.isBloqueado()) {
            menuJugadorBloqueado();
            return;
        }

        System.out.println("=== MENU JUGADOR " + jugador.getNick() + " ===");
        System.out.println("1. Crear personaje");
        System.out.println("2. Ver personaje");
        System.out.println("3. Dar de baja personaje");
        System.out.println("4. Elegir equipo activo");
        System.out.println("5. Desafiar a otro jugador");
        System.out.println("6. Consultar oro ganado/perdido");
        System.out.println("7. Consultar ranking global");
        System.out.println("8. Ver notificaciones");
        System.out.println("9. Ver historial de combates");
        System.out.println("10. Darse de baja");
        System.out.println("11. Cerrar sesion");
        int opcion = input.leerEnteroRango("Selecciona una opcion: ", 1, 11);
        switch (opcion) {
            case 1 -> crearPersonajeJugador();
            case 2 -> verPersonajeJugador();
            case 3 -> eliminarPersonajeJugador();
            case 4 -> equiparJugador();
            case 5 -> lanzarDesafio();
            case 6 -> verMovimientosOro();
            case 7 -> verRanking();
            case 8 -> verNotificaciones();
            case 9 -> verHistorialJugador();
            case 10 -> darseDeBaja();
            case 11 -> sistema.cerrarSesion();
            default -> {
            }
        }
    }

    private void menuJugadorBloqueado() {
        System.out.println("=== MENU JUGADOR BLOQUEADO ===");
        System.out.println("1. Ver personaje");
        System.out.println("2. Ver notificaciones");
        System.out.println("3. Consultar oro ganado/perdido");
        System.out.println("4. Consultar ranking global");
        System.out.println("5. Ver historial de combates");
        System.out.println("6. Darse de baja");
        System.out.println("7. Cerrar sesion");
        int opcion = input.leerEnteroRango("Selecciona una opcion: ", 1, 7);
        switch (opcion) {
            case 1 -> verPersonajeJugador();
            case 2 -> verNotificaciones();
            case 3 -> verMovimientosOro();
            case 4 -> verRanking();
            case 5 -> verHistorialJugador();
            case 6 -> darseDeBaja();
            case 7 -> sistema.cerrarSesion();
            default -> {
            }
        }
    }

    private void menuOperador() {
        Operador operador = (Operador) sistema.getUsuarioActual();
        System.out.println("=== MENU OPERADOR " + operador.getNick() + " ===");
        System.out.println("1. Listar jugadores");
        System.out.println("2. Reemplazar personaje completo de un jugador");
        System.out.println("3. Anadir arma a un jugador");
        System.out.println("4. Anadir armadura a un jugador");
        System.out.println("5. Anadir fortaleza/debilidad a un jugador");
        System.out.println("6. Anadir esbirro a un jugador");
        System.out.println("7. Configurar equipo activo de un jugador");
        System.out.println("8. Validar desafios pendientes");
        System.out.println("9. Bloquear jugador");
        System.out.println("10. Desbloquear jugador");
        System.out.println("11. Consultar ranking global");
        System.out.println("12. Ver historial de un jugador");
        System.out.println("13. Ver notificaciones");
        System.out.println("14. Darse de baja");
        System.out.println("15. Cerrar sesion");
        int opcion = input.leerEnteroRango("Selecciona una opcion: ", 1, 15);
        switch (opcion) {
            case 1 -> listarJugadores();
            case 2 -> reemplazarPersonajeJugador();
            case 3 -> addArmaJugador();
            case 4 -> addArmaduraJugador();
            case 5 -> addModificadorJugador();
            case 6 -> addEsbirroJugador();
            case 7 -> equiparPersonajeJugadorDesdeOperador();
            case 8 -> validarDesafiosPendientes();
            case 9 -> bloquearJugador();
            case 10 -> desbloquearJugador();
            case 11 -> verRanking();
            case 12 -> verHistorialOperador();
            case 13 -> verNotificaciones();
            case 14 -> darseDeBaja();
            case 15 -> sistema.cerrarSesion();
            default -> {
            }
        }
    }

    private void crearPersonajeJugador() {
        ConfiguracionPersonaje configuracion = crearConfiguracionPersonaje();
        Personaje personaje = sistema.crearPersonajeParaJugadorActual(configuracion);
        System.out.println("Personaje creado correctamente.");
        System.out.println(personaje.resumenDetallado());
    }

    private void reemplazarPersonajeJugador() {
        String nick = input.leerNoVacio("Nick del jugador a editar: ");
        ConfiguracionPersonaje configuracion = crearConfiguracionPersonaje();
        Personaje personaje = sistema.reemplazarPersonajeJugador(nick, configuracion);
        System.out.println("Personaje reemplazado correctamente.");
        System.out.println(personaje.resumenDetallado());
    }

    private ConfiguracionPersonaje crearConfiguracionPersonaje() {
        ConfiguracionPersonaje configuracion = new ConfiguracionPersonaje();
        TipoPersonaje tipo = seleccionarTipoPersonaje();
        configuracion.setTipo(tipo);
        configuracion.setNombre(input.leerNoVacio("Nombre del personaje: "));
        configuracion.setPoder(input.leerEnteroRango("Poder (1-5): ", 1, 5));
        configuracion.setOro(Math.max(0, input.leerEntero("Oro inicial (>=0): ")));
        configuracion.setHabilidadEspecial(crearHabilidad(tipo));

        switch (tipo) {
            case VAMPIRO -> {
                configuracion.setEdadVampiro(Math.max(1, input.leerEntero("Edad del vampiro: ")));
                configuracion.setSangreInicial(input.leerEnteroRango("Puntos de sangre iniciales (0-10): ", 0, 10));
            }
            case LICANTROPO -> {
                configuracion.setIncrementoAltura(input.leerDecimalRango("Incremento de altura en metros (0.5 - 1.0): ", 0.5, 1.0));
                configuracion.setIncrementoPeso(input.leerEnteroRango("Incremento de peso en kg (90-110): ", 90, 110));
            }
            case CAZADOR -> {
            }
        }

        int totalArmas = Math.max(0, input.leerEntero("Cuantas armas quieres crear para este personaje: "));
        for (int i = 0; i < totalArmas; i++) {
            configuracion.getArmas().add(crearArma());
        }

        int totalArmaduras = Math.max(0, input.leerEntero("Cuantas armaduras quieres crear para este personaje: "));
        for (int i = 0; i < totalArmaduras; i++) {
            configuracion.getArmaduras().add(crearArmadura());
        }

        int totalFortalezas = Math.max(0, input.leerEntero("Cuantas fortalezas quieres crear: "));
        for (int i = 0; i < totalFortalezas; i++) {
            configuracion.getFortalezas().add(crearModificador(TipoModificador.FORTALEZA));
        }

        int totalDebilidades = Math.max(0, input.leerEntero("Cuantas debilidades quieres crear: "));
        for (int i = 0; i < totalDebilidades; i++) {
            configuracion.getDebilidades().add(crearModificador(TipoModificador.DEBILIDAD));
        }

        int totalEsbirros = Math.max(0, input.leerEntero("Cuantos esbirros quieres crear: "));
        for (int i = 0; i < totalEsbirros; i++) {
            configuracion.getEsbirros().add(crearEsbirro(0));
        }

        if (!configuracion.getArmas().isEmpty()) {
            mostrarNombres("Armas disponibles", configuracion.getArmas().stream().map(Arma::getNombre).toList());
            configuracion.getNombresArmasActivas().addAll(input.leerListaCSV("Introduce el nombre de las armas activas separado por comas: "));
        }
        if (!configuracion.getArmaduras().isEmpty()) {
            mostrarNombres("Armaduras disponibles", configuracion.getArmaduras().stream().map(Armadura::getNombre).toList());
            configuracion.setNombreArmaduraActiva(input.leerNoVacio("Introduce el nombre de la armadura activa: "));
        }

        return configuracion;
    }

    private TipoPersonaje seleccionarTipoPersonaje() {
        System.out.println("Tipo de personaje:");
        System.out.println("1. Vampiro");
        System.out.println("2. Licantropo");
        System.out.println("3. Cazador");
        int opcion = input.leerEnteroRango("Selecciona el tipo: ", 1, 3);
        return switch (opcion) {
            case 1 -> TipoPersonaje.VAMPIRO;
            case 2 -> TipoPersonaje.LICANTROPO;
            case 3 -> TipoPersonaje.CAZADOR;
            default -> throw new IllegalStateException();
        };
    }

    private HabilidadEspecial crearHabilidad(TipoPersonaje tipo) {
        String nombre = input.leerNoVacio("Nombre de la habilidad especial: ");
        int ataque = input.leerEnteroRango("Ataque de la habilidad (1-3): ", 1, 3);
        int defensa = input.leerEnteroRango("Defensa de la habilidad (1-3): ", 1, 3);
        return switch (tipo) {
            case VAMPIRO -> new Disciplina(nombre, ataque, defensa, input.leerEnteroRango("Coste de sangre (1-3): ", 1, 3));
            case LICANTROPO -> new Don(
                    nombre,
                    ataque,
                    defensa,
                    input.leerEnteroRango("Rabia minima requerida (0-3): ", 0, 3),
                    input.leerSiNo("Este don incrementa la rabia al usarse")
            );
            case CAZADOR -> new Talento(nombre, ataque, defensa);
        };
    }

    private Arma crearArma() {
        String nombre = input.leerNoVacio("Nombre del arma: ");
        int ataque = input.leerEnteroRango("Ataque del arma (1-3): ", 1, 3);
        TipoMano tipoMano = input.leerEnteroRango("Tipo de mano (1=una mano, 2=dos manos): ", 1, 2) == 1 ? TipoMano.UNA_MANO : TipoMano.DOS_MANOS;
        Arma arma = new ArmaBase(nombre, ataque, tipoMano);
        if (input.leerSiNo("Quieres anadir defensa extra al arma")) {
            int defensa = input.leerEnteroRango("Defensa extra (1-3): ", 1, 3);
            arma = new ArmaConDefensa(arma, defensa);
        }
        return arma;
    }

    private Armadura crearArmadura() {
        String nombre = input.leerNoVacio("Nombre de la armadura: ");
        int defensa = input.leerEnteroRango("Defensa de la armadura (1-3): ", 1, 3);
        Armadura armadura = new ArmaduraBase(nombre, defensa);
        if (input.leerSiNo("Quieres anadir ataque extra a la armadura")) {
            int ataque = input.leerEnteroRango("Ataque extra (1-3): ", 1, 3);
            armadura = new ArmaduraConAtaque(armadura, ataque);
        }
        return armadura;
    }

    private Modificador crearModificador(TipoModificador tipo) {
        String nombre = input.leerNoVacio("Nombre del modificador " + tipo + ": ");
        int valor = input.leerEnteroRango("Valor del modificador (1-5): ", 1, 5);
        return new Modificador(nombre, valor, tipo);
    }

    private UnidadControlable crearEsbirro(int profundidad) {
        System.out.println("Tipo de esbirro:");
        System.out.println("1. Humano");
        System.out.println("2. Ghoul");
        System.out.println("3. Demonio");
        int opcion = input.leerEnteroRango("Selecciona el tipo de esbirro: ", 1, 3);
        String nombre = input.leerNoVacio("Nombre del esbirro: ");
        int salud = input.leerEnteroRango("Salud del esbirro (1-3): ", 1, 3);
        return switch (opcion) {
            case 1 -> new EsbirroHumano(nombre, salud, seleccionarLealtad());
            case 2 -> new EsbirroGhoul(nombre, salud, input.leerEnteroRango("Dependencia del ghoul (1-5): ", 1, 5));
            case 3 -> {
                EsbirroDemonio demonio = new EsbirroDemonio(nombre, salud, input.leerNoVacio("Descripcion del pacto: "));
                int subordinados = Math.max(0, input.leerEntero("Numero de esbirros subordinados del demonio: "));
                for (int i = 0; i < subordinados; i++) {
                    System.out.println("Creando subordinado " + (i + 1) + " del demonio " + nombre + "...");
                    demonio.addSubordinado(crearEsbirro(profundidad + 1));
                }
                yield demonio;
            }
            default -> throw new IllegalStateException();
        };
    }

    private Lealtad seleccionarLealtad() {
        System.out.println("Lealtad del humano:");
        System.out.println("1. ALTA");
        System.out.println("2. NORMAL");
        System.out.println("3. BAJA");
        int opcion = input.leerEnteroRango("Selecciona la lealtad: ", 1, 3);
        return switch (opcion) {
            case 1 -> Lealtad.ALTA;
            case 2 -> Lealtad.NORMAL;
            case 3 -> Lealtad.BAJA;
            default -> throw new IllegalStateException();
        };
    }

    private void verPersonajeJugador() {
        Jugador jugador = (Jugador) sistema.getUsuarioActual();
        if (!jugador.tienePersonaje()) {
            System.out.println("No tienes personaje registrado.");
            return;
        }
        System.out.println(jugador.getPersonaje().resumenDetallado());
    }

    private void eliminarPersonajeJugador() {
        if (input.leerSiNo("Seguro que quieres dar de baja tu personaje actual")) {
            sistema.eliminarPersonajeActual();
            System.out.println("Personaje eliminado correctamente.");
        }
    }

    private void equiparJugador() {
        Jugador jugador = (Jugador) sistema.getUsuarioActual();
        if (!jugador.tienePersonaje()) {
            System.out.println("No tienes personaje.");
            return;
        }
        Personaje personaje = jugador.getPersonaje();
        mostrarNombres("Armas del inventario", personaje.getArmas().stream().map(Arma::getNombre).toList());
        List<String> armas = input.leerListaCSV("Introduce las armas activas separadas por comas: ");
        mostrarNombres("Armaduras del inventario", personaje.getArmaduras().stream().map(Armadura::getNombre).toList());
        String armadura = input.leerNoVacio("Introduce la armadura activa: ");
        sistema.equiparJugadorActual(armas, armadura);
        System.out.println("Equipo activo actualizado.");
    }

    private void lanzarDesafio() {
        String nickDesafiado = input.leerNoVacio("Nick del jugador desafiado: ");
        int apuesta = input.leerEntero("Cantidad de oro apostada: ");
        Desafio desafio = sistema.lanzarDesafio(nickDesafiado, apuesta);
        System.out.println("Desafio creado correctamente con id " + desafio.getId() + ". Queda pendiente de validacion por un operador.");
    }

    private void verMovimientosOro() {
        List<MovimientoOro> movimientos = sistema.listarMovimientosOroJugadorActual();
        if (movimientos.isEmpty()) {
            System.out.println("No hay movimientos de oro registrados.");
            return;
        }
        movimientos.forEach(System.out::println);
    }

    private void verRanking() {
        List<EntradaRanking> ranking = sistema.consultarRanking();
        if (ranking.isEmpty()) {
            System.out.println("Todavia no hay personajes en el ranking.");
            return;
        }
        ranking.forEach(System.out::println);
    }

    private void verNotificaciones() {
        List<String> notificaciones = sistema.verNotificacionesActuales();
        if (notificaciones.isEmpty()) {
            System.out.println("No hay notificaciones.");
            return;
        }
        notificaciones.forEach(System.out::println);
        if (input.leerSiNo("Quieres limpiar la bandeja de notificaciones")) {
            sistema.limpiarNotificacionesActuales();
            System.out.println("Bandeja vaciada.");
        }
    }

    private void verHistorialJugador() {
        List<RegistroCombate> registros = sistema.listarHistorialJugadorActual();
        mostrarRegistrosCombate(registros);
    }

    private void verHistorialOperador() {
        String nick = input.leerNoVacio("Nick del jugador cuyo historial quieres ver: ");
        List<RegistroCombate> registros = sistema.listarHistorialDeJugador(nick);
        mostrarRegistrosCombate(registros);
    }

    private void mostrarRegistrosCombate(List<RegistroCombate> registros) {
        if (registros.isEmpty()) {
            System.out.println("No hay combates registrados.");
            return;
        }
        int indice = 1;
        for (RegistroCombate registro : registros) {
            System.out.println(indice++ + ". " + registro);
        }
        if (input.leerSiNo("Quieres ver el detalle completo de los eventos")) {
            for (RegistroCombate registro : registros) {
                System.out.println("=== " + registro + " ===");
                registro.getEventos().forEach(System.out::println);
            }
        }
    }

    private void procesarDesafiosPendientes() {
        List<Desafio> pendientes = sistema.listarDesafiosPendientesJugadorActual();
        while (!pendientes.isEmpty()) {
            Desafio desafio = pendientes.get(0);
            System.out.println("Tienes un desafio pendiente: " + desafio);
            System.out.println("Mientras exista un desafio pendiente, no puedes hacer otra accion.");
            System.out.println("1. Aceptar desafio");
            System.out.println("2. Rechazar desafio");
            int opcion = input.leerEnteroRango("Selecciona una opcion: ", 1, 2);
            if (opcion == 1) {
                if (input.leerSiNo("Quieres cambiar tu equipo activo antes de combatir")) {
                    equiparJugador();
                }
                RegistroCombate registro = sistema.aceptarDesafio(desafio.getId());
                System.out.println("Resultado del combate:");
                System.out.println(registro);
                registro.getEventos().forEach(System.out::println);
            } else {
                sistema.rechazarDesafio(desafio.getId());
                System.out.println("Desafio rechazado. Se ha aplicado la penalizacion correspondiente.");
            }
            pendientes = sistema.listarDesafiosPendientesJugadorActual();
        }
    }

    private void listarJugadores() {
        List<Jugador> jugadores = sistema.listarJugadores();
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores registrados.");
            return;
        }
        for (Jugador jugador : jugadores) {
            String personaje = jugador.tienePersonaje() ? jugador.getPersonaje().resumenCorto() : "sin personaje";
            System.out.println(jugador + " | bloqueado=" + jugador.isBloqueado() + " | " + personaje);
        }
    }

    private void addArmaJugador() {
        String nick = input.leerNoVacio("Nick del jugador: ");
        sistema.addArmaAJugador(nick, crearArma());
        System.out.println("Arma anadida correctamente.");
    }

    private void addArmaduraJugador() {
        String nick = input.leerNoVacio("Nick del jugador: ");
        sistema.addArmaduraAJugador(nick, crearArmadura());
        System.out.println("Armadura anadida correctamente.");
    }

    private void addModificadorJugador() {
        String nick = input.leerNoVacio("Nick del jugador: ");
        System.out.println("1. Fortaleza");
        System.out.println("2. Debilidad");
        int opcion = input.leerEnteroRango("Selecciona el tipo: ", 1, 2);
        TipoModificador tipo = opcion == 1 ? TipoModificador.FORTALEZA : TipoModificador.DEBILIDAD;
        sistema.addModificadorAJugador(nick, crearModificador(tipo));
        System.out.println("Modificador anadido correctamente.");
    }

    private void addEsbirroJugador() {
        String nick = input.leerNoVacio("Nick del jugador: ");
        sistema.addEsbirroAJugador(nick, crearEsbirro(0));
        System.out.println("Esbirro anadido correctamente.");
    }

    private void equiparPersonajeJugadorDesdeOperador() {
        String nick = input.leerNoVacio("Nick del jugador: ");
        Jugador jugador = sistema.buscarJugadorPublico(nick).orElseThrow(() -> new DomainException("No existe ese jugador."));
        if (!jugador.tienePersonaje()) {
            throw new DomainException("El jugador no tiene personaje.");
        }
        Personaje personaje = jugador.getPersonaje();
        mostrarNombres("Armas del inventario", personaje.getArmas().stream().map(Arma::getNombre).toList());
        List<String> armas = input.leerListaCSV("Introduce las armas activas separadas por comas: ");
        mostrarNombres("Armaduras del inventario", personaje.getArmaduras().stream().map(Armadura::getNombre).toList());
        String armadura = input.leerNoVacio("Introduce la armadura activa: ");
        sistema.equiparPersonajeDeJugador(nick, armas, armadura);
        System.out.println("Equipo del jugador actualizado.");
    }

    private void validarDesafiosPendientes() {
        List<Desafio> pendientes = sistema.listarDesafiosPendientesRevision();
        if (pendientes.isEmpty()) {
            System.out.println("No hay desafios pendientes de revision.");
            return;
        }

        for (Desafio desafio : pendientes) {
            System.out.println("=== Revisando " + desafio + " ===");
            Jugador desafiante = sistema.buscarJugadorPublico(desafio.getNickDesafiante()).orElseThrow();
            Jugador desafiado = sistema.buscarJugadorPublico(desafio.getNickDesafiado()).orElseThrow();
            if (desafiante.tienePersonaje()) {
                mostrarNombres("Modificadores del desafiante", combinarModificadores(desafiante.getPersonaje()));
            }
            if (desafiado.tienePersonaje()) {
                mostrarNombres("Modificadores del desafiado", combinarModificadores(desafiado.getPersonaje()));
            }
            List<String> presentesDesafiante = input.leerListaCSV("Introduce los modificadores presentes del desafiante separados por comas: ");
            List<String> presentesDesafiado = input.leerListaCSV("Introduce los modificadores presentes del desafiado separados por comas: ");
            sistema.validarDesafio(desafio.getId(), presentesDesafiante, presentesDesafiado);
            System.out.println("Desafio procesado.");
        }
    }

    private List<String> combinarModificadores(Personaje personaje) {
        List<String> nombres = new java.util.ArrayList<>();
        nombres.addAll(personaje.getFortalezas().stream().map(Modificador::getNombre).toList());
        nombres.addAll(personaje.getDebilidades().stream().map(Modificador::getNombre).toList());
        return nombres;
    }

    private void bloquearJugador() {
        String nick = input.leerNoVacio("Nick del jugador a bloquear: ");
        String motivo = input.leerNoVacio("Motivo del bloqueo: ");
        sistema.bloquearJugador(nick, motivo);
        System.out.println("Jugador bloqueado correctamente.");
    }

    private void desbloquearJugador() {
        String nick = input.leerNoVacio("Nick del jugador a desbloquear: ");
        sistema.desbloquearJugador(nick);
        System.out.println("Jugador desbloqueado correctamente.");
    }

    private void darseDeBaja() {
        if (input.leerSiNo("Seguro que quieres darte de baja")) {
            sistema.darBajaCuentaActual();
            System.out.println("Cuenta eliminada.");
        }
    }

    private void mostrarNombres(String titulo, List<String> nombres) {
        System.out.println(titulo + ":");
        if (nombres.isEmpty()) {
            System.out.println("  - ninguno");
            return;
        }
        for (String nombre : nombres) {
            System.out.println("  - " + nombre);
        }
    }
}
