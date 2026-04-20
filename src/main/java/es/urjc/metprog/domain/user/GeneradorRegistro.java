package es.urjc.metprog.domain.user;

import java.util.Locale;
import java.util.Random;
import java.util.Set;

public final class GeneradorRegistro {
    private static final String LETRAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private GeneradorRegistro() {
    }

    public static String generar(Set<String> existentes) {
        Random random = new Random();
        String candidato;
        do {
            candidato = "" + letra(random)
                    + random.nextInt(10)
                    + random.nextInt(10)
                    + letra(random)
                    + letra(random);
        } while (existentes.contains(candidato.toUpperCase(Locale.ROOT)));
        return candidato;
    }

    private static char letra(Random random) {
        return LETRAS.charAt(random.nextInt(LETRAS.length()));
    }
}
