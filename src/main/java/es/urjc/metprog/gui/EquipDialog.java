package es.urjc.metprog.gui;

import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.List;
import java.util.Optional;

final class EquipDialog {
    private EquipDialog() {
    }

    static Optional<EquipSelection> show(Component parent, Personaje personaje) {
        DefaultListModel<Arma> weaponModel = new DefaultListModel<>();
        for (Arma arma : personaje.getArmas()) {
            weaponModel.addElement(arma);
        }
        JList<Arma> weapons = new JList<>(weaponModel);
        weapons.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        Ui.styleList(weapons);
        weapons.setCellRenderer((list, value, index, selected, focus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel(value.descripcion());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
            label.setBackground(selected ? new java.awt.Color(57, 69, 92) : Ui.BG_2);
            label.setForeground(Ui.TEXT);
            return label;
        });
        for (int i = 0; i < weaponModel.size(); i++) {
            Arma arma = weaponModel.get(i);
            if (personaje.getArmasActivas().stream().anyMatch(active -> active.getNombre().equalsIgnoreCase(arma.getNombre()))) {
                weapons.addSelectionInterval(i, i);
            }
        }

        JComboBox<Armadura> armor = new JComboBox<>();
        DefaultComboBoxModel<Armadura> armorModel = new DefaultComboBoxModel<>();
        for (Armadura armadura : personaje.getArmaduras()) {
            armorModel.addElement(armadura);
        }
        armor.setModel(armorModel);
        Ui.styleInput(armor);
        armor.setRenderer((list, value, index, selected, focus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel(value == null ? "Sin armadura" : value.descripcion());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 8, 6, 8));
            label.setBackground(selected ? new java.awt.Color(57, 69, 92) : Ui.BG_2);
            label.setForeground(Ui.TEXT);
            return label;
        });
        if (personaje.getArmaduraActiva() != null) {
            for (int i = 0; i < armorModel.getSize(); i++) {
                if (armorModel.getElementAt(i).getNombre().equalsIgnoreCase(personaje.getArmaduraActiva().getNombre())) {
                    armor.setSelectedIndex(i);
                    break;
                }
            }
        }

        JPanel grid = Ui.transparent(new GridLayout(1, 2, 12, 12));
        grid.add(Ui.titled("Armas activas", Ui.scroll(weapons)));
        JPanel armorPanel = Ui.transparent(new BorderLayout(8, 8));
        armorPanel.add(armor, BorderLayout.NORTH);
        grid.add(Ui.titled("Armadura activa", armorPanel));

        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(grid, BorderLayout.CENTER);
        if (!Ui.confirmDialog(parent, "Configurar equipo activo", root)) {
            return Optional.empty();
        }
        List<String> weaponNames = weapons.getSelectedValuesList().stream().map(Arma::getNombre).toList();
        Armadura selectedArmor = (Armadura) armor.getSelectedItem();
        return Optional.of(new EquipSelection(weaponNames, selectedArmor == null ? "" : selectedArmor.getNombre()));
    }
}
