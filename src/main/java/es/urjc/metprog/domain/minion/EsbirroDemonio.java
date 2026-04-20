package es.urjc.metprog.domain.minion;

import es.urjc.metprog.domain.common.Validaciones;

import java.util.ArrayList;
import java.util.List;

public class EsbirroDemonio extends EsbirroBase {
    private final String pacto;
    private final GrupoEsbirros subordinados;

    public EsbirroDemonio(String nombre, int salud, String pacto) {
        super(nombre, salud);
        Validaciones.noVacio(pacto, "pacto del demonio");
        this.pacto = pacto.trim();
        this.subordinados = new GrupoEsbirros("Subordinados de " + nombre);
    }

    public void addSubordinado(UnidadControlable esbirro) {
        subordinados.add(esbirro);
    }

    public GrupoEsbirros getSubordinados() {
        return subordinados;
    }

    public String getPacto() {
        return pacto;
    }

    @Override
    public int getSaludTotal() {
        return getSaludPropia() + subordinados.getSaludTotal();
    }

    @Override
    public int aplicarDanio(int puntos) {
        int restante = subordinados.aplicarDanio(puntos);
        return super.aplicarDanio(restante);
    }

    @Override
    public void restaurar() {
        subordinados.restaurar();
        super.restaurar();
    }

    @Override
    public boolean estaDerrotado() {
        return super.estaDerrotado() && subordinados.estaDerrotado();
    }

    @Override
    public List<String> listarSupervivientes() {
        List<String> supervivientes = new ArrayList<>(super.listarSupervivientes());
        supervivientes.addAll(subordinados.listarSupervivientes());
        return supervivientes;
    }

    @Override
    public String descripcion() {
        return getNombre() + " [demonio, salud=" + getSaludPropia() + ", pacto=" + pacto + ", subordinados={" + subordinados.descripcion() + "}]";
    }
}
