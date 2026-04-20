package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;

public class Cazador extends Personaje {
    private int voluntadActual;

    public Cazador(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro) {
        super(nombre, habilidadEspecial, poder, oro);
        this.voluntadActual = 3;
    }

    @Override
    public TipoPersonaje getTipo() {
        return TipoPersonaje.CAZADOR;
    }

    public int getVoluntadActual() {
        return voluntadActual;
    }

    public void reducirVoluntad(int puntos) {
        voluntadActual = Math.max(0, voluntadActual - puntos);
    }

    @Override
    public String descripcionRasgo() {
        return "voluntadActual=" + voluntadActual;
    }

    @Override
    protected void prepararRecursoCombate() {
        voluntadActual = 3;
        setEstado(EstadoPersonaje.normal());
    }

    @Override
    protected void onDanioRecibido() {
        reducirVoluntad(1);
    }

    @Override
    protected void actualizarEstadoEspecifico() {
        setEstado(EstadoPersonaje.normal());
    }
}
