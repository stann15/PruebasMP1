package es.urjc.metprog.tests;

import es.urjc.metprog.domain.ability.Disciplina;
import es.urjc.metprog.domain.ability.Don;
import es.urjc.metprog.domain.ability.Talento;
import es.urjc.metprog.domain.character.Cazador;
import es.urjc.metprog.domain.character.ConfiguracionPersonaje;
import es.urjc.metprog.domain.character.DirectorPersonaje;
import es.urjc.metprog.domain.character.Licantropo;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.character.TipoPersonaje;
import es.urjc.metprog.domain.character.Vampiro;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.ArmaduraBase;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;

final class TestFixtures {
    private TestFixtures() {
    }

    static Cazador cazadorEquipado(String nombre, int poder, int oro) {
        return (Cazador) construir(configCazador(nombre, poder, oro));
    }

    static Vampiro vampiroEquipado(String nombre, int poder, int oro, int sangre) {
        return (Vampiro) construir(configVampiro(nombre, poder, oro, sangre));
    }

    static Licantropo licantropoEquipado(String nombre, int poder, int oro) {
        return (Licantropo) construir(configLicantropo(nombre, poder, oro));
    }

    static Personaje construir(ConfiguracionPersonaje configuracion) {
        return new DirectorPersonaje().construir(configuracion);
    }

    static ConfiguracionPersonaje configCazador(String nombre, int poder, int oro) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.CAZADOR);
        cfg.setNombre(nombre);
        cfg.setPoder(poder);
        cfg.setOro(oro);
        cfg.setHabilidadEspecial(new Talento("Fe", 2, 1));
        cfg.getArmas().add(new ArmaBase("Pistola", 1, TipoMano.UNA_MANO));
        cfg.getArmas().add(new ArmaBase("Estaca", 2, TipoMano.UNA_MANO));
        cfg.getArmas().add(new ArmaBase("Mandoble", 3, TipoMano.DOS_MANOS));
        cfg.getArmaduras().add(new ArmaduraBase("Cuero", 1));
        cfg.getNombresArmasActivas().add("Pistola");
        cfg.setNombreArmaduraActiva("Cuero");
        return cfg;
    }

    static ConfiguracionPersonaje configVampiro(String nombre, int poder, int oro, int sangre) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.VAMPIRO);
        cfg.setNombre(nombre);
        cfg.setPoder(poder);
        cfg.setOro(oro);
        cfg.setEdadVampiro(180);
        cfg.setSangreInicial(sangre);
        cfg.setHabilidadEspecial(new Disciplina("Dominacion", 2, 2, 1));
        cfg.getArmas().add(new ArmaBase("Daga", 2, TipoMano.UNA_MANO));
        cfg.getArmaduras().add(new ArmaduraBase("Capa", 1));
        cfg.getFortalezas().add(new Modificador("Noche", 2, TipoModificador.FORTALEZA));
        cfg.getDebilidades().add(new Modificador("Plata", 1, TipoModificador.DEBILIDAD));
        cfg.getNombresArmasActivas().add("Daga");
        cfg.setNombreArmaduraActiva("Capa");
        return cfg;
    }

    static ConfiguracionPersonaje configLicantropo(String nombre, int poder, int oro) {
        ConfiguracionPersonaje cfg = new ConfiguracionPersonaje();
        cfg.setTipo(TipoPersonaje.LICANTROPO);
        cfg.setNombre(nombre);
        cfg.setPoder(poder);
        cfg.setOro(oro);
        cfg.setIncrementoAltura(0.7);
        cfg.setIncrementoPeso(100);
        cfg.setHabilidadEspecial(new Don("Garra", 2, 1, 1, true));
        cfg.getArmas().add(new ArmaBase("Garras", 2, TipoMano.UNA_MANO));
        cfg.getArmaduras().add(new ArmaduraBase("Pelaje", 1));
        cfg.getNombresArmasActivas().add("Garras");
        cfg.setNombreArmaduraActiva("Pelaje");
        return cfg;
    }
}
