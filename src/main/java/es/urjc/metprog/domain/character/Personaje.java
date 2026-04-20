package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.common.Validaciones;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.EsbirroHumano;
import es.urjc.metprog.domain.minion.GrupoEsbirros;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public abstract class Personaje implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    protected static final int SALUD_MAXIMA = 5;

    private String nombre;
    private HabilidadEspecial habilidadEspecial;
    private final List<Arma> armas;
    private final List<Armadura> armaduras;
    private final GrupoEsbirros esbirros;
    private final List<Modificador> fortalezas;
    private final List<Modificador> debilidades;
    private final List<Arma> armasActivas;
    private Armadura armaduraActiva;
    private int oro;
    private int poder;
    private int saludActual;
    private EstadoPersonaje estado;

    protected Personaje(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro) {
        setNombre(nombre);
        setHabilidadEspecial(habilidadEspecial);
        setPoder(poder);
        setOro(oro);
        this.armas = new ArrayList<>();
        this.armaduras = new ArrayList<>();
        this.esbirros = new GrupoEsbirros("Esbirros");
        this.fortalezas = new ArrayList<>();
        this.debilidades = new ArrayList<>();
        this.armasActivas = new ArrayList<>();
        this.saludActual = SALUD_MAXIMA;
        this.estado = EstadoPersonaje.normal();
    }

    public abstract TipoPersonaje getTipo();

    public abstract String descripcionRasgo();

    protected abstract void prepararRecursoCombate();

    protected abstract void onDanioRecibido();

    protected abstract void actualizarEstadoEspecifico();

    public void prepararParaCombate() {
        this.saludActual = SALUD_MAXIMA;
        this.esbirros.restaurar();
        prepararRecursoCombate();
        actualizarEstado();
    }

    public void actualizarEstado() {
        if (saludActual <= 0) {
            estado = EstadoPersonaje.muerto();
            return;
        }
        actualizarEstadoEspecifico();
    }

    protected void setEstado(EstadoPersonaje estado) {
        this.estado = estado;
    }

    public EstadoPersonaje getEstado() {
        return estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        Validaciones.noVacio(nombre, "nombre del personaje");
        this.nombre = nombre.trim();
    }

    public HabilidadEspecial getHabilidadEspecial() {
        return habilidadEspecial;
    }

    public void setHabilidadEspecial(HabilidadEspecial habilidadEspecial) {
        this.habilidadEspecial = Objects.requireNonNull(habilidadEspecial, "La habilidad especial es obligatoria.");
    }

    public int getPoder() {
        return poder;
    }

    public void setPoder(int poder) {
        Validaciones.rango(poder, 1, 5, "poder");
        this.poder = poder;
    }

    public int getOro() {
        return oro;
    }

    public void setOro(int oro) {
        Validaciones.minimo(oro, 0, "oro");
        this.oro = oro;
    }

    public void ajustarOro(int delta) {
        setOro(oro + delta);
    }

    public int getSaludActual() {
        return saludActual;
    }

    public List<Arma> getArmas() {
        return Collections.unmodifiableList(armas);
    }

    public List<Armadura> getArmaduras() {
        return Collections.unmodifiableList(armaduras);
    }

    public List<Arma> getArmasActivas() {
        return Collections.unmodifiableList(armasActivas);
    }

    public Armadura getArmaduraActiva() {
        return armaduraActiva;
    }

    public GrupoEsbirros getEsbirros() {
        return esbirros;
    }

    public List<Modificador> getFortalezas() {
        return Collections.unmodifiableList(fortalezas);
    }

    public List<Modificador> getDebilidades() {
        return Collections.unmodifiableList(debilidades);
    }

    public void addArma(Arma arma) {
        armas.add(arma);
    }

    public void addArmadura(Armadura armadura) {
        armaduras.add(armadura);
    }

    public void addModificador(Modificador modificador) {
        if (modificador.getTipo() == TipoModificador.FORTALEZA) {
            fortalezas.add(modificador);
        } else {
            debilidades.add(modificador);
        }
    }

    public void addEsbirro(UnidadControlable esbirro) {
        if (this instanceof Vampiro && esbirro instanceof EsbirroHumano) {
            throw new DomainException("Los vampiros no pueden tener esbirros humanos.");
        }
        esbirros.add(esbirro);
    }

    public void equiparArmadura(String nombreArmadura) {
        Armadura armadura = armaduras.stream()
                .filter(item -> item.getNombre().equalsIgnoreCase(nombreArmadura))
                .findFirst()
                .orElseThrow(() -> new DomainException("No existe la armadura indicada en el inventario."));
        this.armaduraActiva = armadura;
    }

    public void equiparArmas(List<String> nombresArmas) {
        if (nombresArmas == null || nombresArmas.isEmpty()) {
            throw new DomainException("Debes seleccionar al menos un arma activa.");
        }
        List<Arma> seleccionadas = new ArrayList<>();
        for (String nombreArma : nombresArmas) {
            Arma arma = armas.stream()
                    .filter(item -> item.getNombre().equalsIgnoreCase(nombreArma))
                    .findFirst()
                    .orElseThrow(() -> new DomainException("No existe el arma " + nombreArma + " en el inventario."));
            seleccionadas.add(arma);
        }
        validarConfiguracionArmas(seleccionadas);
        armasActivas.clear();
        armasActivas.addAll(seleccionadas);
    }

    private void validarConfiguracionArmas(List<Arma> seleccionadas) {
        if (seleccionadas.size() > 2) {
            throw new DomainException("Solo se pueden equipar una o dos armas activas.");
        }
        long dosManos = seleccionadas.stream().filter(arma -> arma.getTipoMano() == TipoMano.DOS_MANOS).count();
        if (dosManos > 1) {
            throw new DomainException("No se pueden equipar dos armas de dos manos.");
        }
        if (dosManos == 1 && seleccionadas.size() > 1) {
            throw new DomainException("Si se equipa un arma de dos manos, debe ser la unica arma activa.");
        }
    }

    public boolean tieneEquipoActivo() {
        return armaduraActiva != null && !armasActivas.isEmpty();
    }

    public int getAtaqueEquipoActivo() {
        int ataqueArmas = armasActivas.stream().mapToInt(Arma::getAtaque).sum();
        int ataqueArmadura = armaduraActiva == null ? 0 : armaduraActiva.getAtaque();
        return ataqueArmas + ataqueArmadura;
    }

    public int getDefensaEquipoActivo() {
        int defensaArmas = armasActivas.stream().mapToInt(Arma::getDefensa).sum();
        int defensaArmadura = armaduraActiva == null ? 0 : armaduraActiva.getDefensa();
        return defensaArmas + defensaArmadura;
    }

    public int getSaludTotalEsbirros() {
        return esbirros.getSaludTotal();
    }

    public List<String> getEsbirrosSupervivientes() {
        return esbirros.listarSupervivientes();
    }

    public void aplicarDanio(int puntos) {
        for (int i = 0; i < puntos; i++) {
            int restante = esbirros.aplicarDanio(1);
            if (restante > 0 && saludActual > 0) {
                saludActual--;
                onDanioRecibido();
            }
            actualizarEstado();
        }
    }

    public boolean estaDerrotado() {
        return saludActual <= 0;
    }

    public int calcularImpactoModificadores(List<String> presentes) {
        int fort = fortalezas.stream()
                .filter(modificador -> contiene(presentes, modificador.getNombre()))
                .mapToInt(Modificador::getValor)
                .sum();
        int deb = debilidades.stream()
                .filter(modificador -> contiene(presentes, modificador.getNombre()))
                .mapToInt(Modificador::getValor)
                .sum();
        return fort - deb;
    }

    private boolean contiene(List<String> presentes, String nombre) {
        return presentes.stream().anyMatch(item -> item.trim().equalsIgnoreCase(nombre));
    }

    public String resumenCorto() {
        return getTipo() + " " + nombre + " [salud=" + saludActual + ", poder=" + poder + ", oro=" + oro + ", estado=" + estado.nombre() + "]";
    }

    public String resumenDetallado() {
        List<String> nombresArmas = armas.stream().map(Arma::descripcion).toList();
        List<String> nombresArmasActivas = armasActivas.stream().map(Arma::getNombre).toList();
        List<String> nombresArmaduras = armaduras.stream().map(Armadura::descripcion).toList();
        List<String> fortalezasTexto = fortalezas.stream().map(Modificador::toString).toList();
        List<String> debilidadesTexto = debilidades.stream().map(Modificador::toString).toList();
        return """
                Tipo: %s
                Nombre: %s
                Poder: %d
                Oro: %d
                Salud actual: %d
                Habilidad: %s
                Rasgo: %s
                Armas: %s
                Armas activas: %s
                Armaduras: %s
                Armadura activa: %s
                Fortalezas: %s
                Debilidades: %s
                Esbirros: %s
                """.formatted(
                getTipo(),
                nombre,
                poder,
                oro,
                saludActual,
                habilidadEspecial.resumen(),
                descripcionRasgo(),
                textoLista(nombresArmas),
                textoLista(nombresArmasActivas),
                textoLista(nombresArmaduras),
                armaduraActiva == null ? "ninguna" : armaduraActiva.getNombre(),
                textoLista(fortalezasTexto),
                textoLista(debilidadesTexto),
                esbirros.descripcion()
        );
    }

    private String textoLista(List<String> elementos) {
        return elementos.isEmpty() ? "[]" : elementos.toString();
    }

    public String claveNormalizada() {
        return nombre.toUpperCase(Locale.ROOT);
    }
}
