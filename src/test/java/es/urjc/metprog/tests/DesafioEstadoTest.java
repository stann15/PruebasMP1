package es.urjc.metprog.tests;

import es.urjc.metprog.domain.challenge.Desafio;

import java.util.List;

final class DesafioEstadoTest {
    private DesafioEstadoTest() {
    }

    static void controlaTransicionesLegalesEInvalidas() {
        Desafio desafio = new Desafio("DES-T1", "alice", "bob", 10);
        TestSupport.assertEquals("PENDIENTE", desafio.getNombreEstado(), "Un desafio nace pendiente de revision.");
        TestSupport.assertDomainException(desafio::aceptar, "Un desafio pendiente no puede aceptarse antes de validarse.");

        desafio.validar("op", List.of("Noche"), List.of("Lluvia"));
        TestSupport.assertEquals("VALIDADO", desafio.getNombreEstado(), "Validar publica el desafio.");
        TestSupport.assertEquals(List.of("Noche"), desafio.getPresentesDesafiante(), "La validacion guarda modificadores del desafiante.");
        TestSupport.assertDomainException(
                () -> desafio.validar("op2", List.of(), List.of()),
                "Un desafio ya validado no puede validarse de nuevo."
        );

        desafio.aceptar();
        TestSupport.assertEquals("ACEPTADO", desafio.getNombreEstado(), "Aceptar cambia el estado del desafio.");
        TestSupport.assertDomainException(
                () -> desafio.rechazar("No quiero"),
                "Un desafio aceptado no puede rechazarse."
        );
        desafio.resolver();
        TestSupport.assertTrue(desafio.estaResuelto(), "Un desafio resuelto queda cerrado.");
    }

    static void calculaPenalizacionYRechazoAdministrativo() {
        Desafio desafio = new Desafio("DES-T2", "alice", "bob", 11);
        TestSupport.assertEquals(2, desafio.getPenalizacionRechazo(), "La penalizacion por rechazo es el techo del 10% de la apuesta.");
        desafio.rechazarAdministrativamente("Regla de 24 horas");
        TestSupport.assertEquals("RECHAZADO", desafio.getNombreEstado(), "El rechazo administrativo cierra el desafio.");
        TestSupport.assertContains(desafio.getMotivoRechazo(), "24 horas", "El motivo administrativo queda registrado.");
        TestSupport.assertDomainException(desafio::aceptar, "Un desafio rechazado no puede aceptarse.");
    }
}
