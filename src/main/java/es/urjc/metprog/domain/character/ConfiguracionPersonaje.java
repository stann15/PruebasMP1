package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionPersonaje {
    private TipoPersonaje tipo;
    private String nombre;
    private int poder;
    private int oro;
    private HabilidadEspecial habilidadEspecial;
    private final List<Arma> armas = new ArrayList<>();
    private final List<Armadura> armaduras = new ArrayList<>();
    private final List<Modificador> fortalezas = new ArrayList<>();
    private final List<Modificador> debilidades = new ArrayList<>();
    private final List<UnidadControlable> esbirros = new ArrayList<>();
    private final List<String> nombresArmasActivas = new ArrayList<>();
    private String nombreArmaduraActiva;
    private int edadVampiro;
    private int sangreInicial;
    private double incrementoAltura;
    private int incrementoPeso;

    public TipoPersonaje getTipo() {
        return tipo;
    }

    public void setTipo(TipoPersonaje tipo) {
        this.tipo = tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPoder() {
        return poder;
    }

    public void setPoder(int poder) {
        this.poder = poder;
    }

    public int getOro() {
        return oro;
    }

    public void setOro(int oro) {
        this.oro = oro;
    }

    public HabilidadEspecial getHabilidadEspecial() {
        return habilidadEspecial;
    }

    public void setHabilidadEspecial(HabilidadEspecial habilidadEspecial) {
        this.habilidadEspecial = habilidadEspecial;
    }

    public List<Arma> getArmas() {
        return armas;
    }

    public List<Armadura> getArmaduras() {
        return armaduras;
    }

    public List<Modificador> getFortalezas() {
        return fortalezas;
    }

    public List<Modificador> getDebilidades() {
        return debilidades;
    }

    public List<UnidadControlable> getEsbirros() {
        return esbirros;
    }

    public List<String> getNombresArmasActivas() {
        return nombresArmasActivas;
    }

    public String getNombreArmaduraActiva() {
        return nombreArmaduraActiva;
    }

    public void setNombreArmaduraActiva(String nombreArmaduraActiva) {
        this.nombreArmaduraActiva = nombreArmaduraActiva;
    }

    public int getEdadVampiro() {
        return edadVampiro;
    }

    public void setEdadVampiro(int edadVampiro) {
        this.edadVampiro = edadVampiro;
    }

    public int getSangreInicial() {
        return sangreInicial;
    }

    public void setSangreInicial(int sangreInicial) {
        this.sangreInicial = sangreInicial;
    }

    public double getIncrementoAltura() {
        return incrementoAltura;
    }

    public void setIncrementoAltura(double incrementoAltura) {
        this.incrementoAltura = incrementoAltura;
    }

    public int getIncrementoPeso() {
        return incrementoPeso;
    }

    public void setIncrementoPeso(int incrementoPeso) {
        this.incrementoPeso = incrementoPeso;
    }
}
