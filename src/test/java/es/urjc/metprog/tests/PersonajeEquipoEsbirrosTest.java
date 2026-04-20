package es.urjc.metprog.tests;

import es.urjc.metprog.domain.character.Cazador;
import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.ArmaConDefensa;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.equipment.ArmaduraBase;
import es.urjc.metprog.domain.equipment.ArmaduraConAtaque;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.EsbirroGhoul;
import es.urjc.metprog.domain.minion.EsbirroHumano;
import es.urjc.metprog.domain.minion.Lealtad;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;

import java.util.List;

final class PersonajeEquipoEsbirrosTest {
    private PersonajeEquipoEsbirrosTest() {
    }

    static void equipaDosArmasDeUnaManoYRechazaDosManosCombinada() {
        Personaje cazador = TestFixtures.cazadorEquipado("Armero", 3, 20);
        cazador.equiparArmas(List.of("Pistola", "Estaca"));
        TestSupport.assertEquals(2, cazador.getArmasActivas().size(), "Se pueden activar dos armas de una mano.");

        TestSupport.assertDomainException(
                () -> cazador.equiparArmas(List.of("Mandoble", "Pistola")),
                "Un arma de dos manos no se puede combinar con otra arma activa."
        );
    }

    static void decoradoresDeEquipoSumanAtaqueYDefensa() {
        Cazador cazador = TestFixtures.cazadorEquipado("Decorado", 3, 20);
        Arma armaDefensiva = new ArmaConDefensa(new ArmaBase("Espada Parapeto", 2, TipoMano.UNA_MANO), 2);
        Armadura armaduraOfensiva = new ArmaduraConAtaque(new ArmaduraBase("Armadura de Pinchos", 2), 1);
        cazador.addArma(armaDefensiva);
        cazador.addArmadura(armaduraOfensiva);
        cazador.equiparArmas(List.of("Espada Parapeto"));
        cazador.equiparArmadura("Armadura de Pinchos");

        TestSupport.assertEquals(3, cazador.getAtaqueEquipoActivo(), "El ataque suma arma y ataque extra de armadura.");
        TestSupport.assertEquals(4, cazador.getDefensaEquipoActivo(), "La defensa suma armadura y defensa extra de arma.");
    }

    static void esbirrosAbsorbenDanioAntesQueElPersonajeYSeRestauran() {
        Cazador cazador = TestFixtures.cazadorEquipado("Protegido", 3, 20);
        cazador.addEsbirro(new EsbirroGhoul("Ghoul", 2, 3));

        cazador.aplicarDanio(1);
        TestSupport.assertEquals(5, cazador.getSaludActual(), "El primer punto de dano lo absorbe el esbirro.");
        TestSupport.assertEquals(1, cazador.getSaludTotalEsbirros(), "La salud del esbirro baja tras absorber dano.");

        cazador.aplicarDanio(2);
        TestSupport.assertEquals(4, cazador.getSaludActual(), "El dano sobrante llega al personaje.");
        TestSupport.assertEquals(2, cazador.getVoluntadActual(), "El cazador pierde voluntad al perder salud.");

        cazador.prepararParaCombate();
        TestSupport.assertEquals(5, cazador.getSaludActual(), "Preparar combate restaura salud del personaje.");
        TestSupport.assertEquals(2, cazador.getSaludTotalEsbirros(), "Preparar combate restaura esbirros.");
        TestSupport.assertEquals(3, cazador.getVoluntadActual(), "Preparar combate restaura voluntad del cazador.");
    }

    static void vampiroRechazaHumanosYCalculaModificadoresSinDistinguirMayusculas() {
        Personaje vampiro = TestFixtures.vampiroEquipado("Nocturno", 3, 20, 7);
        TestSupport.assertDomainException(
                () -> vampiro.addEsbirro(new EsbirroHumano("Humano", 1, Lealtad.ALTA)),
                "Un vampiro no puede incorporar esbirros humanos."
        );
        vampiro.addModificador(new Modificador("Luna llena", 2, TipoModificador.FORTALEZA));
        vampiro.addModificador(new Modificador("Fuego", 1, TipoModificador.DEBILIDAD));

        int impacto = vampiro.calcularImpactoModificadores(List.of("luna llena", "FUEGO"));
        TestSupport.assertEquals(1, impacto, "Fortalezas y debilidades presentes se aplican de forma insensible a mayusculas.");
    }
}
