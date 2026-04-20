package es.urjc.metprog.domain.character;

import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;

import java.util.List;

public class DirectorPersonaje {
    public Personaje construir(ConfiguracionPersonaje configuracion) {
        PersonajeBuilder builder = switch (configuracion.getTipo()) {
            case VAMPIRO -> new VampiroBuilder(new VampiroFactory());
            case LICANTROPO -> new LicantropoBuilder(new LicantropoFactory());
            case CAZADOR -> new CazadorBuilder(new CazadorFactory());
        };
        return builder
                .crearBase(configuracion.getNombre(), configuracion.getHabilidadEspecial(), configuracion.getPoder(), configuracion.getOro(), configuracion)
                .addArmas(configuracion.getArmas())
                .addArmaduras(configuracion.getArmaduras())
                .addFortalezas(configuracion.getFortalezas())
                .addDebilidades(configuracion.getDebilidades())
                .addEsbirros(configuracion.getEsbirros())
                .configurarActivos(configuracion.getNombresArmasActivas(), configuracion.getNombreArmaduraActiva())
                .construir();
    }
}

abstract class PersonajeFactory {
    abstract Personaje crear(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, ConfiguracionPersonaje configuracion);
}

final class VampiroFactory extends PersonajeFactory {
    @Override
    Personaje crear(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, ConfiguracionPersonaje configuracion) {
        return new Vampiro(nombre, habilidadEspecial, poder, oro, configuracion.getEdadVampiro(), configuracion.getSangreInicial());
    }
}

final class LicantropoFactory extends PersonajeFactory {
    @Override
    Personaje crear(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, ConfiguracionPersonaje configuracion) {
        return new Licantropo(nombre, habilidadEspecial, poder, oro, configuracion.getIncrementoAltura(), configuracion.getIncrementoPeso());
    }
}

final class CazadorFactory extends PersonajeFactory {
    @Override
    Personaje crear(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, ConfiguracionPersonaje configuracion) {
        return new Cazador(nombre, habilidadEspecial, poder, oro);
    }
}

abstract class PersonajeBuilder {
    private final PersonajeFactory factory;
    protected Personaje personaje;

    protected PersonajeBuilder(PersonajeFactory factory) {
        this.factory = factory;
    }

    public PersonajeBuilder crearBase(String nombre, HabilidadEspecial habilidadEspecial, int poder, int oro, ConfiguracionPersonaje configuracion) {
        this.personaje = factory.crear(nombre, habilidadEspecial, poder, oro, configuracion);
        return this;
    }

    public PersonajeBuilder addArmas(List<Arma> armas) {
        for (Arma arma : armas) {
            personaje.addArma(arma);
        }
        return this;
    }

    public PersonajeBuilder addArmaduras(List<Armadura> armaduras) {
        for (Armadura armadura : armaduras) {
            personaje.addArmadura(armadura);
        }
        return this;
    }

    public PersonajeBuilder addFortalezas(List<Modificador> fortalezas) {
        for (Modificador fortaleza : fortalezas) {
            personaje.addModificador(fortaleza);
        }
        return this;
    }

    public PersonajeBuilder addDebilidades(List<Modificador> debilidades) {
        for (Modificador debilidad : debilidades) {
            personaje.addModificador(debilidad);
        }
        return this;
    }

    public PersonajeBuilder addEsbirros(List<UnidadControlable> esbirros) {
        for (UnidadControlable esbirro : esbirros) {
            personaje.addEsbirro(esbirro);
        }
        return this;
    }

    public PersonajeBuilder configurarActivos(List<String> nombresArmasActivas, String nombreArmaduraActiva) {
        if (!nombresArmasActivas.isEmpty()) {
            personaje.equiparArmas(nombresArmasActivas);
        }
        if (nombreArmaduraActiva != null && !nombreArmaduraActiva.isBlank()) {
            personaje.equiparArmadura(nombreArmaduraActiva);
        }
        return this;
    }

    public Personaje construir() {
        if (personaje == null) {
            throw new DomainException("No se ha construido ningun personaje.");
        }
        return personaje;
    }
}

final class VampiroBuilder extends PersonajeBuilder {
    VampiroBuilder(PersonajeFactory factory) {
        super(factory);
    }
}

final class LicantropoBuilder extends PersonajeBuilder {
    LicantropoBuilder(PersonajeFactory factory) {
        super(factory);
    }
}

final class CazadorBuilder extends PersonajeBuilder {
    CazadorBuilder(PersonajeFactory factory) {
        super(factory);
    }
}
