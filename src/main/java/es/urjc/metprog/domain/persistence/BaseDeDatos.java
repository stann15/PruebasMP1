package es.urjc.metprog.domain.persistence;

import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.combat.RegistroCombate;
import es.urjc.metprog.domain.user.Jugador;
import es.urjc.metprog.domain.user.Operador;
import es.urjc.metprog.domain.user.Usuario;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class BaseDeDatos {
    private static final Path RUTA_DATOS = Path.of("data", "metprog-combate.dat");
    private static final Path RUTA_TEXTO = Path.of("data", "metprog-combate.txt");
    private static final BaseDeDatos INSTANCE = new BaseDeDatos();

    private DatosSistema datos;

    private BaseDeDatos() {
        this.datos = cargar();
        resetearSesiones();
    }

    public static BaseDeDatos getInstance() {
        return INSTANCE;
    }

    public Set<String> getRegistrosUsados() {
        return datos.getJugadores().values().stream().map(Jugador::getNumeroRegistro).collect(java.util.stream.Collectors.toSet());
    }

    public void guardarJugador(Jugador jugador) {
        datos.getJugadores().put(jugador.getNick(), jugador);
        guardar();
    }

    public void guardarOperador(Operador operador) {
        datos.getOperadores().put(operador.getNick(), operador);
        guardar();
    }

    public void eliminarUsuario(String nick) {
        datos.getJugadores().remove(nick);
        datos.getOperadores().remove(nick);
        guardar();
    }

    public Optional<Usuario> buscarUsuario(String nick) {
        Usuario usuario = datos.getJugadores().get(nick);
        if (usuario != null) {
            return Optional.of(usuario);
        }
        return Optional.ofNullable(datos.getOperadores().get(nick));
    }

    public Optional<Jugador> buscarJugador(String nick) {
        return Optional.ofNullable(datos.getJugadores().get(nick));
    }

    public Optional<Operador> buscarOperador(String nick) {
        return Optional.ofNullable(datos.getOperadores().get(nick));
    }

    public List<Jugador> listarJugadores() {
        return new ArrayList<>(datos.getJugadores().values());
    }

    public List<Operador> listarOperadores() {
        return new ArrayList<>(datos.getOperadores().values());
    }

    public String siguienteIdDesafio() {
        String id = datos.siguienteIdDesafio();
        guardar();
        return id;
    }

    public void guardarDesafio(Desafio desafio) {
        datos.getDesafios().put(desafio.getId(), desafio);
        guardar();
    }

    public Optional<Desafio> buscarDesafio(String id) {
        return Optional.ofNullable(datos.getDesafios().get(id));
    }

    public void eliminarDesafio(String id) {
        datos.getDesafios().remove(id);
        guardar();
    }

    public List<Desafio> listarDesafios() {
        return new ArrayList<>(datos.getDesafios().values());
    }

    public List<Desafio> listarDesafiosPendientesRevision() {
        return datos.getDesafios().values().stream()
                .filter(Desafio::estaPendienteRevision)
                .sorted(Comparator.comparing(Desafio::getFechaCreacion))
                .toList();
    }

    public List<Desafio> listarDesafiosPendientesPara(String nick) {
        return datos.getDesafios().values().stream()
                .filter(desafio -> desafio.getNickDesafiado().equalsIgnoreCase(nick))
                .filter(Desafio::estaPendienteRespuesta)
                .sorted(Comparator.comparing(Desafio::getFechaCreacion))
                .toList();
    }

    public List<Desafio> listarDesafiosRelacionados(String nick) {
        return datos.getDesafios().values().stream()
                .filter(desafio -> desafio.getNickDesafiante().equalsIgnoreCase(nick) || desafio.getNickDesafiado().equalsIgnoreCase(nick))
                .sorted(Comparator.comparing(Desafio::getFechaCreacion))
                .toList();
    }

    public void guardarCombate(RegistroCombate combate) {
        datos.getCombates().add(combate);
        guardar();
    }

    public List<RegistroCombate> listarCombates() {
        return new ArrayList<>(datos.getCombates());
    }

    public List<RegistroCombate> listarCombatesDe(String nick) {
        return datos.getCombates().stream()
                .filter(registro -> registro.getNickDesafiante().equalsIgnoreCase(nick) || registro.getNickDesafiado().equalsIgnoreCase(nick))
                .toList();
    }

    public void registrarMovimientoOro(MovimientoOro movimiento) {
        datos.getMovimientosOro().add(movimiento);
        guardar();
    }

    public List<MovimientoOro> listarMovimientosOro(String nick) {
        return datos.getMovimientosOro().stream()
                .filter(movimiento -> movimiento.getNickJugador().equalsIgnoreCase(nick))
                .toList();
    }

    public void persistirCambios() {
        guardar();
    }

    private DatosSistema cargar() {
        if (!Files.exists(RUTA_DATOS)) {
            return new DatosSistema();
        }
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(RUTA_DATOS))) {
            return (DatosSistema) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new DatosSistema();
        }
    }

    private void guardar() {
        try {
            Files.createDirectories(RUTA_DATOS.getParent());
            try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(RUTA_DATOS))) {
                output.writeObject(datos);
            }
            guardarCopiaLegible();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el sistema de persistencia.", e);
        }
    }

    private void guardarCopiaLegible() throws IOException {
        Files.writeString(RUTA_TEXTO, generarCopiaLegible(), StandardCharsets.UTF_8);
    }

    private String generarCopiaLegible() {
        StringBuilder texto = new StringBuilder();
        appendLine(texto, "METPROG COMBATE FANTASTICO - COPIA LEGIBLE");
        appendLine(texto, "Archivo binario original: " + RUTA_DATOS);
        appendLine(texto, "Este TXT es solo informativo. El programa sigue cargando los datos desde el .dat.");
        appendLine(texto, "");
        appendLine(texto, "Resumen:");
        appendLine(texto, "- Jugadores: " + datos.getJugadores().size());
        appendLine(texto, "- Operadores: " + datos.getOperadores().size());
        appendLine(texto, "- Desafios: " + datos.getDesafios().size());
        appendLine(texto, "- Combates: " + datos.getCombates().size());
        appendLine(texto, "- Movimientos de oro: " + datos.getMovimientosOro().size());

        appendJugadores(texto);
        appendOperadores(texto);
        appendDesafios(texto);
        appendCombates(texto);
        appendMovimientosOro(texto);
        return texto.toString();
    }

    private void appendJugadores(StringBuilder texto) {
        appendSeccion(texto, "JUGADORES");
        if (datos.getJugadores().isEmpty()) {
            appendLine(texto, "No hay jugadores registrados.");
            return;
        }
        for (Jugador jugador : datos.getJugadores().values()) {
            appendLine(texto, "- Nick: " + jugador.getNick());
            appendLine(texto, "  Nombre: " + jugador.getNombre());
            appendLine(texto, "  Registro: " + jugador.getNumeroRegistro());
            appendLine(texto, "  Bloqueado: " + textoBooleano(jugador.isBloqueado()));
            appendLine(texto, "  Motivo bloqueo: " + textoOpcional(jugador.getMotivoBloqueo()));
            appendLine(texto, "  Ultima derrota: " + textoOpcional(jugador.getFechaUltimaDerrota()));
            appendLine(texto, "  Personaje:");
            appendBloque(texto, jugador.tienePersonaje() ? jugador.getPersonaje().resumenDetallado() : "Sin personaje.", 4);
            appendNotificaciones(texto, jugador.getBandeja(), 2);
            appendLine(texto, "");
        }
    }

    private void appendOperadores(StringBuilder texto) {
        appendSeccion(texto, "OPERADORES");
        if (datos.getOperadores().isEmpty()) {
            appendLine(texto, "No hay operadores registrados.");
            return;
        }
        for (Operador operador : datos.getOperadores().values()) {
            appendLine(texto, "- Nick: " + operador.getNick());
            appendLine(texto, "  Nombre: " + operador.getNombre());
            appendLine(texto, "  Bloqueado: " + textoBooleano(operador.isBloqueado()));
            appendLine(texto, "  Motivo bloqueo: " + textoOpcional(operador.getMotivoBloqueo()));
            appendNotificaciones(texto, operador.getBandeja(), 2);
            appendLine(texto, "");
        }
    }

    private void appendDesafios(StringBuilder texto) {
        appendSeccion(texto, "DESAFIOS");
        if (datos.getDesafios().isEmpty()) {
            appendLine(texto, "No hay desafios registrados.");
            return;
        }
        for (Desafio desafio : datos.getDesafios().values()) {
            appendLine(texto, "- ID: " + desafio.getId());
            appendLine(texto, "  Estado: " + desafio.getNombreEstado());
            appendLine(texto, "  Desafiante: " + desafio.getNickDesafiante());
            appendLine(texto, "  Desafiado: " + desafio.getNickDesafiado());
            appendLine(texto, "  Apuesta: " + desafio.getApuesta());
            appendLine(texto, "  Fecha creacion: " + desafio.getFechaCreacion());
            appendLine(texto, "  Operador: " + textoOpcional(desafio.getNickOperador()));
            appendLine(texto, "  Presentes desafiante: " + textoLista(desafio.getPresentesDesafiante()));
            appendLine(texto, "  Presentes desafiado: " + textoLista(desafio.getPresentesDesafiado()));
            appendLine(texto, "  Motivo rechazo: " + textoOpcional(desafio.getMotivoRechazo()));
            appendLine(texto, "");
        }
    }

    private void appendCombates(StringBuilder texto) {
        appendSeccion(texto, "COMBATES");
        if (datos.getCombates().isEmpty()) {
            appendLine(texto, "No hay combates registrados.");
            return;
        }
        for (RegistroCombate combate : datos.getCombates()) {
            appendLine(texto, "- Fecha: " + combate.getFecha());
            appendLine(texto, "  Desafiante: " + combate.getNickDesafiante());
            appendLine(texto, "  Desafiado: " + combate.getNickDesafiado());
            appendLine(texto, "  Rondas: " + combate.getRondas());
            appendLine(texto, "  Vencedor: " + (combate.esEmpate() ? "EMPATE" : combate.getVencedor()));
            appendLine(texto, "  Oro ganado: " + combate.getOroGanado());
            appendLine(texto, "  Esbirros supervivientes desafiante: " + textoLista(combate.getEsbirrosSupervivientesDesafiante()));
            appendLine(texto, "  Esbirros supervivientes desafiado: " + textoLista(combate.getEsbirrosSupervivientesDesafiado()));
            appendLine(texto, "  Eventos:");
            appendListaBloque(texto, combate.getEventos(), 4);
            appendLine(texto, "");
        }
    }

    private void appendMovimientosOro(StringBuilder texto) {
        appendSeccion(texto, "MOVIMIENTOS DE ORO");
        if (datos.getMovimientosOro().isEmpty()) {
            appendLine(texto, "No hay movimientos de oro registrados.");
            return;
        }
        for (MovimientoOro movimiento : datos.getMovimientosOro()) {
            appendLine(texto, "- Fecha: " + movimiento.getFecha());
            appendLine(texto, "  Jugador: " + movimiento.getNickJugador());
            appendLine(texto, "  Concepto: " + movimiento.getConcepto());
            appendLine(texto, "  Delta: " + movimiento.getDelta());
            appendLine(texto, "  Saldo resultante: " + movimiento.getSaldoResultante());
            appendLine(texto, "");
        }
    }

    private void appendNotificaciones(StringBuilder texto, List<?> notificaciones, int espacios) {
        appendLine(texto, " ".repeat(espacios) + "Notificaciones:");
        appendListaBloque(texto, notificaciones.stream().map(Object::toString).toList(), espacios + 2);
    }

    private void appendListaBloque(StringBuilder texto, List<String> valores, int espacios) {
        if (valores.isEmpty()) {
            appendLine(texto, " ".repeat(espacios) + "[]");
            return;
        }
        for (String valor : valores) {
            appendLine(texto, " ".repeat(espacios) + "- " + valor);
        }
    }

    private void appendBloque(StringBuilder texto, String contenido, int espacios) {
        String prefijo = " ".repeat(espacios);
        contenido.strip().lines().forEach(linea -> appendLine(texto, prefijo + linea));
    }

    private void appendSeccion(StringBuilder texto, String titulo) {
        appendLine(texto, "");
        appendLine(texto, titulo);
        appendLine(texto, "=".repeat(titulo.length()));
    }

    private String textoLista(List<String> valores) {
        return valores.isEmpty() ? "[]" : valores.toString();
    }

    private String textoOpcional(Object valor) {
        return valor == null ? "N/A" : valor.toString();
    }

    private String textoBooleano(boolean valor) {
        return valor ? "si" : "no";
    }

    private void appendLine(StringBuilder texto, String linea) {
        texto.append(linea).append(System.lineSeparator());
    }

    private void resetearSesiones() {
        datos.getJugadores().values().forEach(Usuario::cerrarSesion);
        datos.getOperadores().values().forEach(Usuario::cerrarSesion);
        guardar();
    }
}
