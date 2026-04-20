package es.urjc.metprog.tests;

import es.urjc.metprog.domain.common.DomainException;
import es.urjc.metprog.domain.persistence.BaseDeDatos;
import es.urjc.metprog.domain.persistence.DatosSistema;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;

final class TestSupport {
    private static final Path DATA_FILE = Path.of("data", "metprog-combate.dat");
    private static final Path TEXT_FILE = Path.of("data", "metprog-combate.txt");

    private TestSupport() {
    }

    static void resetPersistence() throws Exception {
        Files.deleteIfExists(DATA_FILE);
        Files.deleteIfExists(TEXT_FILE);
        BaseDeDatos baseDeDatos = BaseDeDatos.getInstance();
        Field datosField = BaseDeDatos.class.getDeclaredField("datos");
        datosField.setAccessible(true);
        datosField.set(baseDeDatos, new DatosSistema());
        baseDeDatos.persistirCambios();
    }

    static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    static void assertFalse(boolean condition, String message) {
        assertTrue(!condition, message);
    }

    static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Esperado=[" + expected + "], obtenido=[" + actual + "]");
        }
    }

    static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " Esperado=[" + expected + "], obtenido=[" + actual + "]");
        }
    }

    static void assertNotEmpty(Collection<?> collection, String message) {
        assertTrue(collection != null && !collection.isEmpty(), message);
    }

    static void assertContains(String text, String expectedFragment, String message) {
        assertTrue(text != null && text.contains(expectedFragment), message + " Texto=[" + text + "]");
    }

    static DomainException assertDomainException(CheckedRunnable runnable, String message) {
        try {
            runnable.run();
        } catch (DomainException ex) {
            return ex;
        } catch (Exception ex) {
            throw new AssertionError(message + " Se lanzo " + ex.getClass().getSimpleName() + " en lugar de DomainException.", ex);
        }
        throw new AssertionError(message + " No se lanzo DomainException.");
    }

    @FunctionalInterface
    interface CheckedRunnable {
        void run() throws Exception;
    }
}
