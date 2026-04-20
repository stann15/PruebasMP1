package es.urjc.metprog.domain.minion;

public class EsbirroHumano extends EsbirroBase {
    private final Lealtad lealtad;

    public EsbirroHumano(String nombre, int salud, Lealtad lealtad) {
        super(nombre, salud);
        this.lealtad = lealtad;
    }

    public Lealtad getLealtad() {
        return lealtad;
    }

    @Override
    public String descripcion() {
        return getNombre() + " [humano, salud=" + getSaludPropia() + ", lealtad=" + lealtad + "]";
    }
}
