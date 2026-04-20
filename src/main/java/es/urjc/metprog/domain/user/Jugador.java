package es.urjc.metprog.domain.user;

import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.common.Validaciones;

import java.io.Serial;
import java.time.LocalDateTime;

public class Jugador extends Usuario {
    @Serial
    private static final long serialVersionUID = 1L;

    private final String numeroRegistro;
    private Personaje personaje;
    private LocalDateTime fechaUltimaDerrota;

    public Jugador(String nombre, String nick, String password, String numeroRegistro) {
        super(nombre, nick, password, RolUsuario.JUGADOR);
        Validaciones.longitudEntre(password, 8, 12, "password del jugador");
        Validaciones.noVacio(numeroRegistro, "numero de registro");
        this.numeroRegistro = numeroRegistro;
    }

    public String getNumeroRegistro() {
        return numeroRegistro;
    }

    public Personaje getPersonaje() {
        return personaje;
    }

    public void setPersonaje(Personaje personaje) {
        this.personaje = personaje;
    }

    public boolean tienePersonaje() {
        return personaje != null;
    }

    public void eliminarPersonaje() {
        this.personaje = null;
    }

    public LocalDateTime getFechaUltimaDerrota() {
        return fechaUltimaDerrota;
    }

    public void registrarDerrota(LocalDateTime fecha) {
        this.fechaUltimaDerrota = fecha;
    }

    @Override
    public String toString() {
        return super.toString() + " [registro=" + numeroRegistro + "]";
    }
}
