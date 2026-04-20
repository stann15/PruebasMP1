package es.urjc.metprog.gui;

import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.ArmaBase;
import es.urjc.metprog.domain.equipment.ArmaConDefensa;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.equipment.ArmaduraBase;
import es.urjc.metprog.domain.equipment.ArmaduraConAtaque;
import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.EsbirroDemonio;
import es.urjc.metprog.domain.minion.EsbirroGhoul;
import es.urjc.metprog.domain.minion.EsbirroHumano;
import es.urjc.metprog.domain.minion.Lealtad;
import es.urjc.metprog.domain.minion.UnidadControlable;
import es.urjc.metprog.domain.modifier.Modificador;
import es.urjc.metprog.domain.modifier.TipoModificador;

import java.util.ArrayList;
import java.util.List;

record WeaponDraft(String name, int attack, TipoMano hand, boolean defenseEnabled, int defenseExtra) {
    Arma build() {
        Arma arma = new ArmaBase(name, attack, hand);
        if (defenseEnabled) {
            arma = new ArmaConDefensa(arma, defenseExtra);
        }
        return arma;
    }

    @Override
    public String toString() {
        String defense = defenseEnabled ? ", def+" + defenseExtra : "";
        return name + " [atk=" + attack + ", " + hand + defense + "]";
    }
}

record ArmorDraft(String name, int defense, boolean attackEnabled, int attackExtra) {
    Armadura build() {
        Armadura armadura = new ArmaduraBase(name, defense);
        if (attackEnabled) {
            armadura = new ArmaduraConAtaque(armadura, attackExtra);
        }
        return armadura;
    }

    @Override
    public String toString() {
        String attack = attackEnabled ? ", atk+" + attackExtra : "";
        return name + " [def=" + defense + attack + "]";
    }
}

record ModifierDraft(String name, int value, TipoModificador type) {
    Modificador build() {
        return new Modificador(name, value, type);
    }

    @Override
    public String toString() {
        return name + " [" + type + ", " + value + "]";
    }
}

enum MinionKind {
    HUMANO("Humano"),
    GHOUL("Ghoul"),
    DEMONIO("Demonio");

    private final String label;

    MinionKind(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}

record MinionDraft(
        MinionKind kind,
        String name,
        int health,
        Lealtad loyalty,
        int dependence,
        String pact,
        List<MinionDraft> children
) {
    MinionDraft {
        children = new ArrayList<>(children);
    }

    UnidadControlable build() {
        return switch (kind) {
            case HUMANO -> new EsbirroHumano(name, health, loyalty);
            case GHOUL -> new EsbirroGhoul(name, health, dependence);
            case DEMONIO -> {
                EsbirroDemonio demon = new EsbirroDemonio(name, health, pact);
                for (MinionDraft child : children) {
                    demon.addSubordinado(child.build());
                }
                yield demon;
            }
        };
    }

    @Override
    public String toString() {
        return switch (kind) {
            case HUMANO -> name + " [humano, salud=" + health + ", lealtad=" + loyalty + "]";
            case GHOUL -> name + " [ghoul, salud=" + health + ", dependencia=" + dependence + "]";
            case DEMONIO -> name + " [demonio, salud=" + health + ", subordinados=" + children.size() + "]";
        };
    }
}

record EquipSelection(List<String> weaponNames, String armorName) {
}
