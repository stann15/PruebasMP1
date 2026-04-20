package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.common.Validaciones;

public class Vampiro extends Personaje {
    private int edad;
    private int sangreActual;

    public Vampiro(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, int edad, int sangreActual) {
        super(nombre, habilidadEspecial, poder, oro);
        setEdad(edad);
        setSangreActual(sangreActual);
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.VAMPIRO;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        Validaciones.minimo(edad, 1, "edad del vampiro");
        this.edad = edad;
    }

    public int getSangreActual() {
        return sangreActual;
    }

    public void setSangreActual(int sangreActual) {
        Validaciones.rango(sangreActual, 0, 10, "reserva de sangre");
        this.sangreActual = sangreActual;
    }

    public boolean puedeConsumirSangre(int coste) {
        return sangreActual >= coste;
    }

    public void consumirSangre(int coste) {
        if (!puedeConsumirSangre(coste)) {
            return;
        }
        sangreActual -= coste;
    }

    public void recuperarSangre(int puntos) {
        sangreActual = Math.min(10, sangreActual + puntos);
    }

    @Override
    public String descripcionRasgo() {
        return "edad=" + edad + ", sangreActual=" + sangreActual;
    }

    @Override
    protected void prepararRecursoCombate() {
        setEstado(EstadoPersonaje.normal());
    }

    @Override
    protected void onDanioRecibido() {
    }

    @Override
    protected void actualizarEstadoEspecifico() {
        setEstado(EstadoPersonaje.normal());
    }
}
