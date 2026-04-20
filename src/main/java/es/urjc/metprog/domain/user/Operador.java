package es.urjc.metprog.domain.user;

import java.io.Serial;

public class Operador extends Usuario {
    @Serial
    private static final long serialVersionUID = 1L;

    public Operador(String nombre, String nick, String password) {
        super(nombre, nick, password, RolUsuario.OPERADOR);
    }
}
