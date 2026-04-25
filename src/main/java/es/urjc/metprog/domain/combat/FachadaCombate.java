package es.urjc.metprog.domain.combat;

import es.urjc.metprog.domain.challenge.Desafio;
import es.urjc.metprog.domain.user.Jugador;

import java.time.LocalDateTime;

public class FachadaCombate {
    public RegistroCombate ejecutarCombate(Desafio desafio, Jugador desafiante, Jugador desafiado) {
        RegistroCombate registro = new Combate(desafio, desafiante, desafiado).ejecutar();

        if (!registro.esEmpate()) {
            Jugador ganador = registro.getVencedor().equals(desafiante.getNick()) ? desafiante : desafiado;
            Jugador perdedor = ganador == desafiante ? desafiado : desafiante;
            ganador.getPersonaje().ajustarOro(registro.getOroGanado());
            perdedor.getPersonaje().ajustarOro(-registro.getOroGanado());
            perdedor.registrarDerrota(LocalDateTime.now());
        }

        registro = registro.conResumenEconomico(
                desafio.getApuesta(),
                desafiante.getPersonaje().getOro(),
                desafiado.getPersonaje().getOro()
        );

        // Los recursos y la salud temporal del combate se restauran al cerrar la batalla.
        desafiante.getPersonaje().prepararParaCombate();
        desafiado.getPersonaje().prepararParaCombate();
        desafio.resolver();
        return registro;
    }
}
