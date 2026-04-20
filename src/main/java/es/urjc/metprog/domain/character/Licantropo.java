package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.common.Validaciones;

public class Licantropo extends Personaje {
    private double incrementoAltura;
    private int incrementoPeso;
    private int rabiaActual;

    public Licantropo(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, double incrementoAltura, int incrementoPeso) {
        super(nombre, habilidadEspecial, poder, oro);
        setIncrementoAltura(incrementoAltura);
        setIncrementoPeso(incrementoPeso);
        this.rabiaActual = 0;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.LICANTROPO;
    }

    public double getIncrementoAltura() {
        return incrementoAltura;
    }

    public void setIncrementoAltura(double incrementoAltura) {
        if (incrementoAltura < 0.5 || incrementoAltura > 1.0) {
            throw new DomainException("El incremento de altura del licantropo debe estar entre 0.5 y 1.0 metros.");
        }
        this.incrementoAltura = incrementoAltura;
    }

    public int getIncrementoPeso() {
        return incrementoPeso;
    }

    public void setIncrementoPeso(int incrementoPeso) {
        if (incrementoPeso < 90 || incrementoPeso > 110) {
            throw new DomainException("El incremento de peso del licantropo debe estar entre 90 y 110 kg.");
        }
        this.incrementoPeso = incrementoPeso;
    }

    public int getRabiaActual() {
        return rabiaActual;
    }

    public void incrementarRabia(int puntos) {
        rabiaActual = Math.min(3, rabiaActual + puntos);
        actualizarEstado();
    }

    public void resetRabia() {
        rabiaActual = 0;
        actualizarEstado();
    }

    @Override
    public String descripcionRasgo() {
        return "rabiaActual=" + rabiaActual + ", incrementoAltura=" + incrementoAltura + "m, incrementoPeso=" + incrementoPeso + "kg";
    }

    @Override
    protected void prepararRecursoCombate() {
        rabiaActual = 0;
        actualizarEstado();
    }

    @Override
    protected void onDanioRecibido() {
        incrementarRabia(1);
    }

    @Override
    protected void actualizarEstadoEspecifico() {
        if (rabiaActual >= 3) {
            setEstado(EstadoPersonaje.bestia());
        } else {
            setEstado(EstadoPersonaje.normal());
        }
    }
}
