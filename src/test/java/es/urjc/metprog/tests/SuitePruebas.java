package es.urjc.metprog.tests;

import java.util.ArrayList;
import java.util.List;

public final class SuitePruebas {
    private SuitePruebas() {
    }

    public static void main(String[] args) throws Exception {
        List<CasoPrueba> casos = new ArrayList<>();
        registrar(casos, "ValidacionesTest.validaCamposObligatoriosYRangos", ValidacionesTest::validaCamposObligatoriosYRangos);
        registrar(casos, "ValidacionesTest.jugadorValidaPasswordYRegistro", ValidacionesTest::jugadorValidaPasswordYRegistro);
        registrar(casos, "ValidacionesTest.oroNuncaPuedeSerNegativo", ValidacionesTest::oroNuncaPuedeSerNegativo);
        registrar(casos, "PersonajeEquipoEsbirrosTest.equipaDosArmasDeUnaManoYRechazaDosManosCombinada", PersonajeEquipoEsbirrosTest::equipaDosArmasDeUnaManoYRechazaDosManosCombinada);
        registrar(casos, "PersonajeEquipoEsbirrosTest.decoradoresDeEquipoSumanAtaqueYDefensa", PersonajeEquipoEsbirrosTest::decoradoresDeEquipoSumanAtaqueYDefensa);
        registrar(casos, "PersonajeEquipoEsbirrosTest.esbirrosAbsorbenDanioAntesQueElPersonajeYSeRestauran", PersonajeEquipoEsbirrosTest::esbirrosAbsorbenDanioAntesQueElPersonajeYSeRestauran);
        registrar(casos, "PersonajeEquipoEsbirrosTest.vampiroRechazaHumanosYCalculaModificadoresSinDistinguirMayusculas", PersonajeEquipoEsbirrosTest::vampiroRechazaHumanosYCalculaModificadoresSinDistinguirMayusculas);
        registrar(casos, "DesafioEstadoTest.controlaTransicionesLegalesEInvalidas", DesafioEstadoTest::controlaTransicionesLegalesEInvalidas);
        registrar(casos, "DesafioEstadoTest.calculaPenalizacionYRechazoAdministrativo", DesafioEstadoTest::calculaPenalizacionYRechazoAdministrativo);
        registrar(casos, "RankingTest.ordenaPorOroVictoriasYNickYCalculaDerrotas", RankingTest::ordenaPorOroVictoriasYNickYCalculaDerrotas);
        registrar(casos, "CombateTest.combateGeneraRondasEventosYResultadoCoherente", CombateTest::combateGeneraRondasEventosYResultadoCoherente);
        registrar(casos, "SistemaFacadeFlujosTest.registraAutenticaYControlaRoles", SistemaFacadeFlujosTest::registraAutenticaYControlaRoles);
        registrar(casos, "SistemaFacadeFlujosTest.desafioValidadoBloqueaAccionesYRechazoPenaliza", SistemaFacadeFlujosTest::desafioValidadoBloqueaAccionesYRechazoPenaliza);
        registrar(casos, "SistemaFacadeFlujosTest.validacionAplicaReglaDe24HorasYBloqueaDesafiante", SistemaFacadeFlujosTest::validacionAplicaReglaDe24HorasYBloqueaDesafiante);
        registrar(casos, "SistemaFacadeFlujosTest.validaApuestaEquipoYPersistenciaBasica", SistemaFacadeFlujosTest::validaApuestaEquipoYPersistenciaBasica);

        int correctos = 0;
        List<String> fallos = new ArrayList<>();
        long inicio = System.currentTimeMillis();
        for (CasoPrueba caso : casos) {
            try {
                caso.ejecutar();
                correctos++;
                System.out.println("[OK] " + caso.nombre);
            } catch (Throwable ex) {
                fallos.add(caso.nombre + " -> " + ex.getMessage());
                System.out.println("[ERROR] " + caso.nombre);
                ex.printStackTrace(System.out);
            }
        }
        long duracion = System.currentTimeMillis() - inicio;
        System.out.println();
        System.out.println("Resumen suite pruebas: " + correctos + "/" + casos.size() + " correctos en " + duracion + " ms.");
        if (!fallos.isEmpty()) {
            System.out.println("Fallos detectados:");
            for (String fallo : fallos) {
                System.out.println("- " + fallo);
            }
            throw new AssertionError("La suite de pruebas ha detectado fallos.");
        }
    }

    private static void registrar(List<CasoPrueba> casos, String nombre, TestSupport.CheckedRunnable runnable) {
        casos.add(new CasoPrueba(nombre, runnable));
    }

    private record CasoPrueba(String nombre, TestSupport.CheckedRunnable ejecutar) {
    }
}
