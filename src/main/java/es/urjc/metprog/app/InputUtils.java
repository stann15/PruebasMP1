package es.urjc.metprog.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputUtils {
    private final Scanner scanner;

    public InputUtils(Scanner scanner) {
        this.scanner = scanner;
    }

    public String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public String leerNoVacio(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            if (!valor.isBlank()) {
                return valor;
            }
            System.out.println("El valor no puede estar vacio.");
        }
    }

    public int leerEntero(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un numero entero valido.");
            }
        }
    }

    public int leerEnteroRango(String mensaje, int min, int max) {
        while (true) {
            int valor = leerEntero(mensaje);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("El valor debe estar entre " + min + " y " + max + ".");
        }
    }

    public double leerDecimalRango(String mensaje, double min, double max) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                double numero = Double.parseDouble(valor);
                if (numero >= min && numero <= max) {
                    return numero;
                }
                System.out.println("El valor debe estar entre " + min + " y " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Debes introducir un numero decimal valido.");
            }
        }
    }

    public boolean leerSiNo(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje + " (s/n): ").toLowerCase();
            if (valor.equals("s")) {
                return true;
            }
            if (valor.equals("n")) {
                return false;
            }
            System.out.println("Responde con 's' o 'n'.");
        }
    }

    public List<String> leerListaCSV(String mensaje) {
        String valor = leerTexto(mensaje);
        if (valor.isBlank()) {
            return List.of();
        }
        String[] partes = valor.split(",");
        List<String> resultado = new ArrayList<>();
        for (String parte : partes) {
            String limpia = parte.trim();
            if (!limpia.isBlank()) {
                resultado.add(limpia);
            }
        }
        return resultado;
    }
}
