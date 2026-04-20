package es.urjc.metprog.domain.common;

public final class Validaciones {
    private Validaciones() {
    }

    public static void noVacio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new DomainException("El campo " + campo + " no puede estar vacio.");
        }
    }

    public static void rango(int valor, int min, int max, String campo) {
        if (valor < min || valor > max) {
            throw new DomainException("El campo " + campo + " debe estar entre " + min + " y " + max + ".");
        }
    }

    public static void minimo(int valor, int min, String campo) {
        if (valor < min) {
            throw new DomainException("El campo " + campo + " debe ser mayor o igual que " + min + ".");
        }
    }

    public static void longitudEntre(String valor, int min, int max, String campo) {
        noVacio(valor, campo);
        if (valor.length() < min || valor.length() > max) {
            throw new DomainException("El campo " + campo + " debe tener entre " + min + " y " + max + " caracteres.");
        }
    }
}
