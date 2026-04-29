package es.urjc.metprog.app;

import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.ConfiguracionPersonaje;
import es.urjc.metprog.domain.character.DirectorPersonaje;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.combat.FachadaCombate;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.notification.PublicadorNotificaciones;
import es.urjc.metprog.domain.notification.TipoNotificacion;
import es.urjc.metprog.domain.persistence.BaseDeDatos;
import es.urjc.metprog.domain.persistence.MovimientoOro;
import es.urjc.metprog.domain.ranking.EntradaRanking;
import es.urjc.metprog.domain.ranking.ServicioRanking;
import es.urjc.metprog.domain.user.GeneradorRegistro;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.Operador;
import es.urjc.metprog.domain.user.RolUsuario;
import es.urjc.metprog.domain.user.Usuario;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SistemaFacade {
    private final BaseDeDatos baseDeDatos;
    private final DirectorPersonaje directorPersonaje;
    private final FachadaCombate fachadaCombate;
    private final ServicioRanking servicioRanking;
    private final PublicadorNotificaciones publicadorNotificaciones;
    private Usuario usuarioActual;

    public SistemaFacade() 
        this.baseDeDatos = BaseDeDatos.getInstance();
        this.directorPersonaje = new DirectorPersonaje();
        this.fachadaCombate = new FachadaCombate();
        this.servicioRanking = new ServicioRanking();
        this.publicadorNotificaciones = new PublicadorNotificaciones();
    }

    public Jugador registrarJugador(String nombre, String nick, String password) {
        asegurarNickLibre(nick);
        String registro = GeneradorRegistro.generar(baseDeDatos.getRegistrosUsados());
        Jugador jugador = new Jugador(nombre, nick, password, registro);
        baseDeDatos.guardarJugador(jugador);
        return jugador;
    }

    public Operador registrarOperador(String nombre, String nick, String password) {
        asegurarNickLibre(nick);
        Operador operador = new Operador(nombre, nick, password);
        baseDeDatos.guardarOperador(operador);
        return operador;
    }

    public Usuario iniciarSesion(String nick, String password) {
        Usuario usuario = baseDeDatos.buscarUsuario(nick)
                .orElseThrow(() -> new DomainException("No existe un usuario con ese nick."));
        if (!usuario.passwordCorrecto(password)) {
            throw new DomainException("La password introducida es incorrecta.");
        }
        usuario.iniciarSesion();
        usuarioActual = usuario;
        baseDeDatos.persistirCambios();
        return usuario;
    }

    public void cerrarSesion() {
        if (usuarioActual != null) {
            usuarioActual.cerrarSesion();
            usuarioActual = null;
            baseDeDatos.persistirCambios();
        }
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public RolUsuario getRolActual() {
        asegurarSesion();
        return usuarioActual.getRol();
    }

    public boolean usuarioActualBloqueado() {
        asegurarSesion();
        return usuarioActual.isBloqueado();
    }

    public void darBajaCuentaActual() {
        asegurarSesion();
        if (usuarioActual instanceof Jugador jugador) {
            asegurarSinDesafioPendiente(jugador);
        }
        String nick = usuarioActual.getNick();
        eliminarDesafiosDeUsuario(nick);
        baseDeDatos.eliminarUsuario(nick);
        usuarioActual = null;
    }

    public Personaje crearPersonajeParaJugadorActual(ConfiguracionPersonaje configuracion) {
        Jugador jugador = asegurarJugadorActual();
        asegurarSinDesafioPendiente(jugador);
        if (jugador.tienePersonaje()) {
            throw new DomainException("El jugador ya tiene un personaje registrado.");
        }
        Personaje personaje = directorPersonaje.construir(configuracion);
        jugador.setPersonaje(personaje);
        baseDeDatos.persistirCambios();
        return personaje;
    }

    public Personaje reemplazarPersonajeJugador(String nickJugador, ConfiguracionPersonaje configuracion) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        Personaje personaje = directorPersonaje.construir(configuracion);
        jugador.setPersonaje(personaje);
        baseDeDatos.persistirCambios();
        return personaje;
    }

    public void eliminarPersonajeActual() {
        Jugador jugador = asegurarJugadorActual();
        asegurarSinDesafioPendiente(jugador);
        jugador.eliminarPersonaje();
        baseDeDatos.persistirCambios();
    }

    public void equiparJugadorActual(List<String> nombresArmas, String nombreArmadura) {
        Jugador jugador = asegurarJugadorActual();
        asegurarSinDesafioPendiente(jugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().equiparArmas(nombresArmas);
        jugador.getPersonaje().equiparArmadura(nombreArmadura);
        baseDeDatos.persistirCambios();
    }

    public void equiparJugadorActualAntesDeAceptarDesafio(String idDesafio, List<String> nombresArmas, String nombreArmadura) {
        Jugador jugador = asegurarJugadorActual();
        asegurarPuedeAjustarEquipoAntesDeAceptarDesafio(jugador, idDesafio);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().equiparArmas(nombresArmas);
        jugador.getPersonaje().equiparArmadura(nombreArmadura);
        baseDeDatos.persistirCambios();
    }

    public void equiparPersonajeDeJugador(String nickJugador, List<String> nombresArmas, String nombreArmadura) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().equiparArmas(nombresArmas);
        jugador.getPersonaje().equiparArmadura(nombreArmadura);
        baseDeDatos.persistirCambios();
    }

    public void addArmaAJugador(String nickJugador, Arma arma) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().addArma(arma);
        baseDeDatos.persistirCambios();
    }

    public void addArmaduraAJugador(String nickJugador, Armadura armadura) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().addArmadura(armadura);
        baseDeDatos.persistirCambios();
    }

    public void addModificadorAJugador(String nickJugador, Modificador modificador) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().addModificador(modificador);
        baseDeDatos.persistirCambios();
    }

    public void addEsbirroAJugador(String nickJugador, UnidadControlable esbirro) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        asegurarPersonaje(jugador);
        jugador.getPersonaje().addEsbirro(esbirro);
        baseDeDatos.persistirCambios();
    }

    public Desafio lanzarDesafio(String nickDesafiado, int apuesta) {
        Jugador desafiante = asegurarJugadorActual();
        asegurarSinDesafioPendiente(desafiante);
        if (desafiante.isBloqueado()) {
            throw new DomainException("Tu cuenta esta bloqueada y no puede lanzar desafios.");
        }
        Jugador desafiado = obtenerJugador(nickDesafiado);
        if (desafiado.isBloqueado()) {
            throw new DomainException("El usuario desafiado esta bloqueado y no puede recibir desafios.");
        }
        if (desafiante.getNick().equalsIgnoreCase(desafiado.getNick())) {
            throw new DomainException("No puedes desafiarte a ti mismo.");
        }
        asegurarPersonaje(desafiante);
        asegurarPersonaje(desafiado);
        if (!desafiante.getPersonaje().tieneEquipoActivo() || !desafiado.getPersonaje().tieneEquipoActivo()) {
            throw new DomainException("Ambos jugadores deben tener equipo activo para poder iniciar un desafio.");
        }
        if (apuesta < 0) {
            throw new DomainException("La apuesta no puede ser negativa.");
        }
        if (apuesta > desafiante.getPersonaje().getOro()) {
            throw new DomainException("No puedes apostar mas oro del que posees.");
        }
        if (apuesta > desafiado.getPersonaje().getOro()) {
            throw new DomainException("El usuario desafiado no dispone de oro suficiente para cubrir la apuesta.");
        }
        if (tieneDesafioBloqueante(desafiante.getNick()) || tieneDesafioBloqueante(desafiado.getNick())) {
            throw new DomainException("Alguno de los usuarios ya tiene un desafio pendiente de resolver.");
        }
        String id = baseDeDatos.siguienteIdDesafio();
        Desafio desafio = new Desafio(id, desafiante.getNick(), desafiado.getNick(), apuesta);
        baseDeDatos.guardarDesafio(desafio);
        return desafio;
    }

    public List<Desafio> listarDesafiosPendientesRevision() {
        asegurarOperadorActual();
        return baseDeDatos.listarDesafiosPendientesRevision();
    }

    public List<Desafio> listarDesafiosPendientesJugadorActual() {
        Jugador jugador = asegurarJugadorActual();
        return baseDeDatos.listarDesafiosPendientesPara(jugador.getNick());
    }

    public void validarDesafio(String idDesafio, List<String> presentesDesafiante, List<String> presentesDesafiado) {
        Operador operador = asegurarOperadorActual();
        Desafio desafio = obtenerDesafio(idDesafio);
        Jugador desafiante = obtenerJugador(desafio.getNickDesafiante());
        Jugador desafiado = obtenerJugador(desafio.getNickDesafiado());

        String motivoBloqueo = motivoRechazoPorBloqueo(desafiante, desafiado);
        if (motivoBloqueo != null) {
            desafio.rechazarAdministrativamente(motivoBloqueo);
            publicadorNotificaciones.notificar(
                    desafiante,
                    TipoNotificacion.DESAFIO_RECHAZADO,
                    "Tu desafio contra " + desafiado.getNick() + " ha sido rechazado. Motivo: " + motivoBloqueo
            );
            baseDeDatos.persistirCambios();
            return;
        }

        LocalDateTime limite = LocalDateTime.now().minusHours(24);
        if (desafiado.getFechaUltimaDerrota() != null && desafiado.getFechaUltimaDerrota().isAfter(limite)) {
            desafiante.bloquear("Desafio invalido: el desafiado habia perdido un combate en las 24 horas previas.");
            desafio.rechazarAdministrativamente("El desafiado habia perdido un combate en las ultimas 24 horas.");
            publicadorNotificaciones.notificar(desafiante, TipoNotificacion.USUARIO_BLOQUEADO, "Has sido bloqueado por incumplir la norma de las 24 horas.");
            baseDeDatos.persistirCambios();
            return;
        }

        desafio.validar(operador.getNick(), presentesDesafiante, presentesDesafiado);
        publicadorNotificaciones.notificar(
                desafiado,
                TipoNotificacion.DESAFIO_RECIBIDO,
                "Tienes un desafio validado de " + desafiante.getNick() + " por " + desafio.getApuesta() + " monedas de oro."
        );
        baseDeDatos.persistirCambios();
    }

    public RegistroCombate aceptarDesafio(String idDesafio) {
        Jugador desafiado = asegurarJugadorActual();
        Desafio desafio = obtenerDesafio(idDesafio);
        if (!desafio.getNickDesafiado().equalsIgnoreCase(desafiado.getNick())) {
            throw new DomainException("Solo el usuario desafiado puede aceptar este desafio.");
        }
        Jugador desafiante = obtenerJugador(desafio.getNickDesafiante());
        desafio.aceptar();
        RegistroCombate registro = fachadaCombate.ejecutarCombate(desafio, desafiante, desafiado);
        baseDeDatos.guardarCombate(registro);
        registrarMovimientosPorCombate(registro, desafiante, desafiado);
        publicadorNotificaciones.notificar(
                desafiante,
                TipoNotificacion.RESULTADO_COMBATE,
                construirResumenCombate(registro)
        );
        baseDeDatos.persistirCambios();
        return registro;
    }

    public void rechazarDesafio(String idDesafio) {
        Jugador desafiado = asegurarJugadorActual();
        Desafio desafio = obtenerDesafio(idDesafio);
        if (!desafio.getNickDesafiado().equalsIgnoreCase(desafiado.getNick())) {
            throw new DomainException("Solo el usuario desafiado puede rechazar este desafio.");
        }
        Jugador desafiante = obtenerJugador(desafio.getNickDesafiante());
        int penalizacion = desafio.getPenalizacionRechazo();
        desafiado.getPersonaje().ajustarOro(-penalizacion);
        desafiante.getPersonaje().ajustarOro(penalizacion);
        desafio.rechazar("El desafiado rechazo el desafio.");
        baseDeDatos.registrarMovimientoOro(new MovimientoOro(desafiado.getNick(), "Penalizacion por rechazar desafio", -penalizacion, desafiado.getPersonaje().getOro()));
        baseDeDatos.registrarMovimientoOro(new MovimientoOro(desafiante.getNick(), "Compensacion por rechazo del desafio", penalizacion, desafiante.getPersonaje().getOro()));
        publicadorNotificaciones.notificar(
                desafiante,
                TipoNotificacion.DESAFIO_RECHAZADO,
                "El usuario " + desafiado.getNick() + " ha rechazado tu desafio. Has recibido " + penalizacion + " monedas."
        );
        baseDeDatos.persistirCambios();
    }

    public List<MovimientoOro> listarMovimientosOroJugadorActual() {
        Jugador jugador = asegurarJugadorActual();
        asegurarSinDesafioPendiente(jugador);
        return baseDeDatos.listarMovimientosOro(jugador.getNick());
    }

    public List<RegistroCombate> listarHistorialJugadorActual() {
        Jugador jugador = asegurarJugadorActual();
        asegurarSinDesafioPendiente(jugador);
        return baseDeDatos.listarCombatesDe(jugador.getNick());
    }

    public List<RegistroCombate> listarHistorialDeJugador(String nickJugador) {
        asegurarOperadorActual();
        return baseDeDatos.listarCombatesDe(nickJugador);
    }

    public List<EntradaRanking> consultarRanking() {
        if (usuarioActual instanceof Jugador jugador) {
            asegurarSinDesafioPendiente(jugador);
        }
        return servicioRanking.calcularRanking(baseDeDatos.listarJugadores(), baseDeDatos.listarCombates());
    }

    public List<Jugador> listarJugadores() {
        asegurarOperadorActual();
        return baseDeDatos.listarJugadores();
    }

    public void bloquearJugador(String nickJugador, String motivo) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        jugador.bloquear(motivo);
        publicadorNotificaciones.notificar(jugador, TipoNotificacion.USUARIO_BLOQUEADO, "Tu cuenta ha sido bloqueada. Motivo: " + motivo);
        baseDeDatos.persistirCambios();
    }

    public void desbloquearJugador(String nickJugador) {
        asegurarOperadorActual();
        Jugador jugador = obtenerJugador(nickJugador);
        jugador.desbloquear();
        publicadorNotificaciones.notificar(jugador, TipoNotificacion.USUARIO_DESBLOQUEADO, "Tu cuenta ha sido desbloqueada por un operador.");
        baseDeDatos.persistirCambios();
    }

    public List<String> verNotificacionesActuales() {
        asegurarSesion();
        if (usuarioActual instanceof Jugador jugador) {
            asegurarSinDesafioPendiente(jugador);
        }
        return usuarioActual.getBandeja().stream().map(Object::toString).toList();
    }

    public void limpiarNotificacionesActuales() {
        asegurarSesion();
        if (usuarioActual instanceof Jugador jugador) {
            asegurarSinDesafioPendiente(jugador);
        }
        usuarioActual.limpiarNotificaciones();
        baseDeDatos.persistirCambios();
    }

    public Optional<Jugador> buscarJugadorPublico(String nick) {
        return baseDeDatos.buscarJugador(nick);
    }

    public List<Desafio> listarDesafiosRelacionadosActual() {
        asegurarSesion();
        if (usuarioActual instanceof Jugador jugador) {
            asegurarSinDesafioPendiente(jugador);
        }
        return baseDeDatos.listarDesafiosRelacionados(usuarioActual.getNick());
    }

    private void registrarMovimientosPorCombate(RegistroCombate registro, Jugador desafiante, Jugador desafiado) {
        if (registro.esEmpate()) {
            baseDeDatos.registrarMovimientoOro(new MovimientoOro(desafiante.getNick(), "Empate en combate", 0, desafiante.getPersonaje().getOro()));
            baseDeDatos.registrarMovimientoOro(new MovimientoOro(desafiado.getNick(), "Empate en combate", 0, desafiado.getPersonaje().getOro()));
            return;
        }
        Jugador ganador = registro.getVencedor().equals(desafiante.getNick()) ? desafiante : desafiado;
        Jugador perdedor = ganador == desafiante ? desafiado : desafiante;
        baseDeDatos.registrarMovimientoOro(new MovimientoOro(ganador.getNick(), "Victoria en combate", registro.getOroGanado(), ganador.getPersonaje().getOro()));
        baseDeDatos.registrarMovimientoOro(new MovimientoOro(perdedor.getNick(), "Derrota en combate", -registro.getOroGanado(), perdedor.getPersonaje().getOro()));
    }

    private boolean tieneDesafioBloqueante(String nick) {
        return baseDeDatos.listarDesafiosRelacionados(nick).stream()
                .anyMatch(desafio -> desafio.estaPendienteRevision() || desafio.estaPendienteRespuesta());
    }

    private String motivoRechazoPorBloqueo(Jugador desafiante, Jugador desafiado) {
        if (desafiante.isBloqueado()) {
            return "El usuario desafiante esta bloqueado.";
        }
        if (desafiado.isBloqueado()) {
            return "El usuario desafiado esta bloqueado.";
        }
        return null;
    }

    private void eliminarDesafiosDeUsuario(String nick) {
        List<Desafio> relacionados = new ArrayList<>(baseDeDatos.listarDesafiosRelacionados(nick));
        for (Desafio desafio : relacionados) {
            baseDeDatos.eliminarDesafio(desafio.getId());
        }
    }

    private void asegurarNickLibre(String nick) {
        if (baseDeDatos.buscarUsuario(nick).isPresent()) {
            throw new DomainException("Ya existe un usuario con ese nick.");
        }
    }

    private void asegurarSesion() {
        if (usuarioActual == null) {
            throw new DomainException("No hay ninguna sesion activa.");
        }
    }

    private Jugador asegurarJugadorActual() {
        asegurarSesion();
        if (!(usuarioActual instanceof Jugador jugador)) {
            throw new DomainException("La operacion requiere una sesion de jugador.");
        }
        return jugador;
    }

    private Operador asegurarOperadorActual() {
        asegurarSesion();
        if (!(usuarioActual instanceof Operador operador)) {
            throw new DomainException("La operacion requiere una sesion de operador.");
        }
        return operador;
    }

    private Jugador obtenerJugador(String nick) {
        return baseDeDatos.buscarJugador(nick)
                .orElseThrow(() -> new DomainException("No existe un jugador con nick " + nick + "."));
    }

    private Desafio obtenerDesafio(String idDesafio) {
        return baseDeDatos.buscarDesafio(idDesafio)
                .orElseThrow(() -> new DomainException("No existe el desafio " + idDesafio + "."));
    }

    private void asegurarPersonaje(Jugador jugador) {
        if (!jugador.tienePersonaje()) {
            throw new DomainException("El jugador " + jugador.getNick() + " no tiene personaje registrado.");
        }
    }

    private void asegurarPuedeAjustarEquipoAntesDeAceptarDesafio(Jugador jugador, String idDesafio) {
        Desafio desafio = obtenerDesafio(idDesafio);
        if (!desafio.getNickDesafiado().equalsIgnoreCase(jugador.getNick())) {
            throw new DomainException("Solo el usuario desafiado puede ajustar su equipo antes del combate.");
        }
        if (!desafio.estaPendienteRespuesta()) {
            throw new DomainException("Solo se puede ajustar el equipo cuando el desafio esta validado y pendiente de aceptar.");
        }
    }

    private void asegurarSinDesafioPendiente(Jugador jugador) {
        if (!baseDeDatos.listarDesafiosPendientesPara(jugador.getNick()).isEmpty()) {
            throw new DomainException("El jugador tiene un desafio pendiente y solo puede aceptarlo o rechazarlo.");
        }
    }

    private String construirResumenCombate(RegistroCombate registro) {
        return "Resultado del combate contra " + registro.getNickDesafiado() + "/" + registro.getNickDesafiante()
                + ": " + (registro.esEmpate() ? "empate" : "vencedor " + registro.getVencedor())
                + ", rondas=" + registro.getRondas();
    }
}
