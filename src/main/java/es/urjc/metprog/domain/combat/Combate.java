package es.urjc.metprog.domain.combat;

import es.urjc.metprog.domain.ability.Disciplina;
import es.urjc.metprog.domain.ability.Don;
import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.character.Cazador;
import es.urjc.metprog.domain.character.Licantropo;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.character.Vampiro;
import es.urjc.metprog.domain.user.Jugador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Combate 
    private final Desafio desafio;
    private final Jugador desafiante;
    private final Jugador desafiado;
    private final Random random;
    private final List<ComandoCombate> comandos;

    public Combate(Desafio desafio, Jugador desafiante, Jugador desafiado) {
        this.desafio = desafio;
        this.desafiante = desafiante;
        this.desafiado = desafiado;
        this.random = new Random();
        this.comandos = List.of(
                new CalcularPotencialesCommand(),
                new ResolverTiradasCommand(),
                new AplicarDanioCommand()
        );
    }

    public RegistroCombate ejecutar() {
        List<String> eventos = new ArrayList<>();
        Personaje atacante = desafiante.getPersonaje();
        Personaje defensor = desafiado.getPersonaje();
        atacante.prepararParaCombate();
        defensor.prepararParaCombate();

        int ronda = 0;
        while (!atacante.estaDerrotado() && !defensor.estaDerrotado() && ronda < 500) {
            ronda++;
            ContextoRonda contexto = new ContextoRonda(ronda, atacante, defensor, desafio, eventos, random);
            for (ComandoCombate comando : comandos) {
                comando.ejecutar(contexto);
            }
        }

        String vencedor = null;
        int oroGanado = 0;
        if (atacante.estaDerrotado() && defensor.estaDerrotado()) {
            eventos.add("El combate termina en empate.");
        } else if (!atacante.estaDerrotado() && defensor.estaDerrotado()) {
            vencedor = desafiante.getNick();
            oroGanado = desafio.getApuesta();
            eventos.add("Vencedor final: " + vencedor);
        } else if (atacante.estaDerrotado()) {
            vencedor = desafiado.getNick();
            oroGanado = desafio.getApuesta();
            eventos.add("Vencedor final: " + vencedor);
        } else {
            eventos.add("El combate se ha cerrado por seguridad tras 500 rondas. Se declara empate tecnico.");
        }

        return new RegistroCombate(
                desafiante.getNick(),
                desafiado.getNick(),
                ronda,
                LocalDateTime.now(),
                vencedor,
                atacante.getEsbirrosSupervivientes(),
                defensor.getEsbirrosSupervivientes(),
                oroGanado,
                eventos
        );
    }
}

interface ComandoCombate {
    void ejecutar(ContextoRonda contexto);
}

final class ContextoRonda {
    final int numeroRonda;
    final Personaje desafiante;
    final Personaje desafiado;
    final Desafio desafio;
    final List<String> eventos;
    final Random random;
    ResultadoPotencial ataqueDesafiante;
    ResultadoPotencial defensaDesafiante;
    ResultadoPotencial ataqueDesafiado;
    ResultadoPotencial defensaDesafiado;
    int exitosAtaqueDesafiante;
    int exitosDefensaDesafiante;
    int exitosAtaqueDesafiado;
    int exitosDefensaDesafiado;

    ContextoRonda(int numeroRonda, Personaje desafiante, Personaje desafiado, Desafio desafio, List<String> eventos, Random random) {
        this.numeroRonda = numeroRonda;
        this.desafiante = desafiado;
        this.desafiado = desafiado;
        this.desafio = desafio;
        this.eventos = eventos;
        this.random = random;
    }
}

record ResultadoPotencial(int potencial, boolean recuperarSangreSiImpacta, String detalle) {
}

final class CalcularPotencialesCommand implements ComandoCombate {
    private final CalculadorPotencial vampiro = new CalculadorVampiro();
    private final CalculadorPotencial licantropo = new CalculadorLicantropo();
    private final CalculadorPotencial cazador = new CalculadorCazador();

    @Override
    public void ejecutar(ContextoRonda contexto) {
        contexto.eventos.add("----- Ronda " + contexto.numeroRonda + " -----");
        contexto.ataqueDesafiante = resolver(contexto.desafiante).calcularAtaque(
                contexto.desafiante,
                contexto.desafio.getPresentesDesafiante(),
                contexto.eventos
        );
        contexto.defensaDesafiante = resolver(contexto.desafiante).calcularDefensa(
                contexto.desafiante,
                contexto.desafio.getPresentesDesafiante(),
                contexto.eventos
        );
        contexto.ataqueDesafiado = resolver(contexto.desafiado).calcularAtaque(
                contexto.desafiado,
                contexto.desafio.getPresentesDesafiado(),
                contexto.eventos
        );
        contexto.defensaDesafiado = resolver(contexto.desafiado).calcularDefensa(
                contexto.desafiado,
                contexto.desafio.getPresentesDesafiado(),
                contexto.eventos
        );
    }

    private CalculadorPotencial resolver(Personaje personaje) {
        return switch (personaje.getTipo()) {
            case VAMPIRO -> vampiro;
            case LICANTROPO -> licantropo;
            case CAZADOR -> cazador;
        };
    }
}

final class ResolverTiradasCommand implements ComandoCombate {
    @Override
    public void ejecutar(ContextoRonda contexto) {
        contexto.exitosAtaqueDesafiante = tirar(contexto.ataqueDesafiante, contexto.eventos, "Ataque " + contexto.desafiante.getNombre(), contexto.random);
        contexto.exitosDefensaDesafiante = tirar(contexto.defensaDesafiante, contexto.eventos, "Defensa " + contexto.desafiante.getNombre(), contexto.random);
        contexto.exitosAtaqueDesafiado = tirar(contexto.ataqueDesafiado, contexto.eventos, "Ataque " + contexto.desafiado.getNombre(), contexto.random);
        contexto.exitosDefensaDesafiado = tirar(contexto.defensaDesafiado, contexto.eventos, "Defensa " + contexto.desafiado.getNombre(), contexto.random);
    }

    private int tirar(ResultadoPotencial resultado, List<String> eventos, String etiqueta, Random random) {
        int exitos = 0;
        List<Integer> tiradas = new ArrayList<>();
        for (int i = 0; i < resultado.potencial(); i++) {
            int dado = random.nextInt(6) + 1;
            tiradas.add(dado);
            if (dado >= 5) {
                exitos++;
            }
        }
        eventos.add(etiqueta + " => potencial=" + resultado.potencial() + ", detalle={" + resultado.detalle() + "}, tiradas=" + tiradas + ", exitos=" + exitos);
        return exitos;
    }
}

final class AplicarDanioCommand implements ComandoCombate {
    @Override
    public void ejecutar(ContextoRonda contexto) {
        boolean impactaDesafiante = contexto.exitosAtaqueDesafiante >= contexto.exitosDefensaDesafiado;
        boolean impactaDesafiado = contexto.exitosAtaqueDesafiado >= contexto.exitosDefensaDesafiante;

        contexto.eventos.add(contexto.desafiante.getNombre() + (impactaDesafiante ? " impacta" : " falla") + " a " + contexto.desafiado.getNombre());
        contexto.eventos.add(contexto.desafiado.getNombre() + (impactaDesafiado ? " impacta" : " falla") + " a " + contexto.desafiante.getNombre());

        if (impactaDesafiante) {
            contexto.desafiado.aplicarDanio(1);
            if (contexto.ataqueDesafiante.recuperarSangreSiImpacta() && contexto.desafiante instanceof Vampiro vampiro) {
                vampiro.recuperarSangre(4);
                contexto.eventos.add(vampiro.getNombre() + " recupera 4 puntos de sangre por un ataque exitoso.");
            }
        }

        if (impactaDesafiado) {
            contexto.desafiante.aplicarDanio(1);
            if (contexto.ataqueDesafiado.recuperarSangreSiImpacta() && contexto.desafiado instanceof Vampiro vampiro) {
                vampiro.recuperarSangre(4);
                contexto.eventos.add(vampiro.getNombre() + " recupera 4 puntos de sangre por un ataque exitoso.");
            }
        }

        contexto.eventos.add("Estado tras ronda: " + contexto.desafiante.resumenCorto() + " || " + contexto.desafiado.resumenCorto());
    }
}

interface CalculadorPotencial {
    ResultadoPotencial calcularAtaque(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos);

    ResultadoPotencial calcularDefensa(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos);
}

final class CalculadorVampiro implements CalculadorPotencial {
    @Override
    public ResultadoPotencial calcularAtaque(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Vampiro vampiro = (Vampiro) personaje;
        Disciplina disciplina = (Disciplina) vampiro.getHabilidadEspecial();
        int sangreBonus = vampiro.getSangreActual() >= 5 ? 2 : 0;
        int habilidad = 0;
        boolean recupera = false;
        if (vampiro.puedeConsumirSangre(disciplina.getCosteSangre())) {
            vampiro.consumirSangre(disciplina.getCosteSangre());
            habilidad = disciplina.getAtaque();
            recupera = true;
        } else {
            eventos.add(vampiro.getNombre() + " no puede usar su disciplina ofensiva por falta de sangre.");
        }
        int potencial = Math.max(0, vampiro.getPoder() + habilidad + vampiro.getAtaqueEquipoActivo() + sangreBonus + vampiro.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, recupera, "vampiro: poder=" + vampiro.getPoder() + ", habilidad=" + habilidad + ", equipo=" + vampiro.getAtaqueEquipoActivo() + ", sangreBonus=" + sangreBonus + ", mod=" + vampiro.calcularImpactoModificadores(modificadoresPresentes));
    }

    @Override
    public ResultadoPotencial calcularDefensa(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Vampiro vampiro = (Vampiro) personaje;
        Disciplina disciplina = (Disciplina) vampiro.getHabilidadEspecial();
        int sangreBonus = vampiro.getSangreActual() >= 5 ? 2 : 0;
        int habilidad = 0;
        if (vampiro.puedeConsumirSangre(disciplina.getCosteSangre())) {
            vampiro.consumirSangre(disciplina.getCosteSangre());
            habilidad = disciplina.getDefensa();
        } else {
            eventos.add(vampiro.getNombre() + " no puede usar su disciplina defensiva por falta de sangre.");
        }
        int potencial = Math.max(0, vampiro.getPoder() + habilidad + vampiro.getDefensaEquipoActivo() + sangreBonus + vampiro.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, false, "vampiro: poder=" + vampiro.getPoder() + ", habilidad=" + habilidad + ", equipo=" + vampiro.getDefensaEquipoActivo() + ", sangreBonus=" + sangreBonus + ", mod=" + vampiro.calcularImpactoModificadores(modificadoresPresentes));
    }
}

final class CalculadorLicantropo implements CalculadorPotencial {
    @Override
    public ResultadoPotencial calcularAtaque(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Licantropo licantropo = (Licantropo) personaje;
        Don don = (Don) licantropo.getHabilidadEspecial();
        int habilidad = 0;
        if (licantropo.getRabiaActual() >= don.getRabiaMinima()) {
            habilidad = don.getAtaque();
            if (don.isIncrementaRabiaAlUsarse()) {
                licantropo.incrementarRabia(1);
            }
        } else {
            eventos.add(licantropo.getNombre() + " no puede usar su don ofensivo: rabia insuficiente.");
        }
        int potencial = Math.max(0, licantropo.getPoder() + habilidad + licantropo.getAtaqueEquipoActivo() + licantropo.getRabiaActual() + licantropo.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, false, "licantropo: poder=" + licantropo.getPoder() + ", habilidad=" + habilidad + ", equipo=" + licantropo.getAtaqueEquipoActivo() + ", rabia=" + licantropo.getRabiaActual() + ", mod=" + licantropo.calcularImpactoModificadores(modificadoresPresentes));
    }

    @Override
    public ResultadoPotencial calcularDefensa(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Licantropo licantropo = (Licantropo) personaje;
        Don don = (Don) licantropo.getHabilidadEspecial();
        int habilidad = 0;
        if (licantropo.getRabiaActual() >= don.getRabiaMinima()) {
            habilidad = don.getDefensa();
            if (don.isIncrementaRabiaAlUsarse()) {
                licantropo.incrementarRabia(1);
            }
        } else {
            eventos.add(licantropo.getNombre() + " no puede usar su don defensivo: rabia insuficiente.");
        }
        int potencial = Math.max(0, licantropo.getPoder() + habilidad + licantropo.getDefensaEquipoActivo() + licantropo.getRabiaActual() + licantropo.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, false, "licantropo: poder=" + licantropo.getPoder() + ", habilidad=" + habilidad + ", equipo=" + licantropo.getDefensaEquipoActivo() + ", rabia=" + licantropo.getRabiaActual() + ", mod=" + licantropo.calcularImpactoModificadores(modificadoresPresentes));
    }
}

final class CalculadorCazador implements CalculadorPotencial {
    @Override
    public ResultadoPotencial calcularAtaque(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Cazador cazador = (Cazador) personaje;
        int habilidad = cazador.getHabilidadEspecial().getAtaque();
        int potencial = Math.max(0, cazador.getPoder() + habilidad + cazador.getAtaqueEquipoActivo() + cazador.getVoluntadActual() + cazador.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, false, "cazador: poder=" + cazador.getPoder() + ", habilidad=" + habilidad + ", equipo=" + cazador.getAtaqueEquipoActivo() + ", voluntad=" + cazador.getVoluntadActual() + ", mod=" + cazador.calcularImpactoModificadores(modificadoresPresentes));
    }

    @Override
    public ResultadoPotencial calcularDefensa(Personaje personaje, List<String> modificadoresPresentes, List<String> eventos) {
        Cazador cazador = (Cazador) personaje;
        int habilidad = cazador.getHabilidadEspecial().getDefensa();
        int potencial = Math.max(0, cazador.getPoder() + habilidad + cazador.getDefensaEquipoActivo() + cazador.getVoluntadActual() + cazador.calcularImpactoModificadores(modificadoresPresentes));
        return new ResultadoPotencial(potencial, false, "cazador: poder=" + cazador.getPoder() + ", habilidad=" + habilidad + ", equipo=" + cazador.getDefensaEquipoActivo() + ", voluntad=" + cazador.getVoluntadActual() + ", mod=" + cazador.calcularImpactoModificadores(modificadoresPresentes));
    }
}
