package es.urjc.metprog.gui;

import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.equipment.Arma;
import es.urjc.metprog.domain.equipment.Armadura;
import es.urjc.metprog.domain.equipment.TipoMano;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class EquipDialog {
    private EquipDialog() {
    }

    static Optional<EquipSelection> show(Component parent, Personaje personaje) {
        DefaultListModel<Arma> inventoryWeapons = new DefaultListModel<>();
        for (Arma arma : personaje.getArmas()) {
            inventoryWeapons.addElement(arma);
        }
        DefaultListModel<Arma> activeWeapons = new DefaultListModel<>();
        for (Arma arma : personaje.getArmasActivas()) {
            activeWeapons.addElement(arma);
        }

        JList<Arma> inventoryWeaponList = new JList<>(inventoryWeapons);
        JList<Arma> activeWeaponList = new JList<>(activeWeapons);
        styleWeaponList(inventoryWeaponList);
        styleWeaponList(activeWeaponList);
        inventoryWeaponList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        activeWeaponList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JButton activateWeapons = Ui.successButton("Activar");
        JButton deactivateWeapons = Ui.secondaryButton("Quitar activa");
        activateWeapons.addActionListener(event -> {
            List<Arma> candidate = new ArrayList<>(values(activeWeapons));
            for (Arma arma : inventoryWeaponList.getSelectedValuesList()) {
                if (candidate.stream().noneMatch(item -> item.getNombre().equalsIgnoreCase(arma.getNombre()))) {
                    candidate.add(arma);
                }
            }
            try {
                validateWeapons(candidate);
                syncModel(activeWeapons, candidate);
            } catch (IllegalArgumentException ex) {
                Ui.messageDialog(parent, "Armas activas", ex.getMessage());
            }
        });
        deactivateWeapons.addActionListener(event -> {
            List<Arma> candidate = new ArrayList<>(values(activeWeapons));
            for (Arma arma : activeWeaponList.getSelectedValuesList()) {
                candidate.removeIf(item -> item.getNombre().equalsIgnoreCase(arma.getNombre()));
            }
            syncModel(activeWeapons, candidate);
        });

        JPanel weaponButtons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        weaponButtons.add(activateWeapons);
        weaponButtons.add(deactivateWeapons);

        JPanel inventoryWeaponsPanel = Ui.transparent(new BorderLayout(8, 8));
        inventoryWeaponsPanel.add(Ui.scroll(inventoryWeaponList), BorderLayout.CENTER);
        inventoryWeaponsPanel.add(Ui.small("Todo el arsenal disponible del personaje."), BorderLayout.SOUTH);

        JPanel activeWeaponsPanel = Ui.transparent(new BorderLayout(8, 8));
        activeWeaponsPanel.add(Ui.scroll(activeWeaponList), BorderLayout.CENTER);
        activeWeaponsPanel.add(weaponButtons, BorderLayout.SOUTH);

        DefaultListModel<Armadura> inventoryArmors = new DefaultListModel<>();
        for (Armadura armadura : personaje.getArmaduras()) {
            inventoryArmors.addElement(armadura);
        }
        JList<Armadura> inventoryArmorList = new JList<>(inventoryArmors);
        styleArmorList(inventoryArmorList);
        inventoryArmorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JComboBox<Armadura> activeArmor = new JComboBox<>();
        DefaultComboBoxModel<Armadura> activeArmorModel = new DefaultComboBoxModel<>();
        for (Armadura armadura : personaje.getArmaduras()) {
            activeArmorModel.addElement(armadura);
        }
        activeArmor.setModel(activeArmorModel);
        Ui.styleInput(activeArmor);
        activeArmor.setPreferredSize(new Dimension(250, 46));
        activeArmor.setRenderer((list, value, index, selected, focus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel(value == null ? "Selecciona armadura activa" : value.descripcion());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 10, 6, 10));
            label.setBackground(selected ? new java.awt.Color(57, 69, 92) : Ui.INPUT);
            label.setForeground(value == null ? Ui.MUTED : Ui.TEXT);
            label.setFont(label.getFont().deriveFont(index < 0 ? Font.BOLD : Font.PLAIN, 13f));
            return label;
        });

        if (personaje.getArmaduraActiva() != null) {
            for (int i = 0; i < activeArmorModel.getSize(); i++) {
                if (activeArmorModel.getElementAt(i).getNombre().equalsIgnoreCase(personaje.getArmaduraActiva().getNombre())) {
                    activeArmor.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            activeArmor.setSelectedItem(null);
        }

        JButton activateArmor = Ui.successButton("Establecer activa");
        activateArmor.addActionListener(event -> {
            Armadura selected = inventoryArmorList.getSelectedValue();
            if (selected == null) {
                Ui.messageDialog(parent, "Armadura activa", "Selecciona primero una armadura del inventario.");
                return;
            }
            activeArmor.setSelectedItem(selected);
        });

        JPanel inventoryArmorPanel = Ui.transparent(new BorderLayout(8, 8));
        inventoryArmorPanel.add(Ui.scroll(inventoryArmorList), BorderLayout.CENTER);
        inventoryArmorPanel.add(Ui.small("Armaduras disponibles para equipar."), BorderLayout.SOUTH);

        JPanel activeArmorPanel = Ui.transparent(new BorderLayout(8, 8));
        activeArmorPanel.add(activeArmor, BorderLayout.NORTH);
        JPanel armorButtons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        armorButtons.add(activateArmor);
        activeArmorPanel.add(armorButtons, BorderLayout.SOUTH);

        JPanel grid = Ui.transparent(new GridLayout(2, 2, 12, 12));
        grid.add(Ui.titled("Inventario de armas", inventoryWeaponsPanel));
        grid.add(Ui.titled("Armas activas", activeWeaponsPanel));
        grid.add(Ui.titled("Inventario de armaduras", inventoryArmorPanel));
        grid.add(Ui.titled("Armadura activa", activeArmorPanel));

        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(grid, BorderLayout.CENTER);

        while (true) {
            if (!Ui.confirmDialog(parent, "Configurar equipo activo", root)) {
                return Optional.empty();
            }
            try {
                List<Arma> selectedWeapons = values(activeWeapons);
                validateWeapons(selectedWeapons);
                Armadura selectedArmor = (Armadura) activeArmor.getSelectedItem();
                if (!personaje.getArmaduras().isEmpty() && selectedArmor == null) {
                    throw new IllegalArgumentException("Selecciona una armadura activa.");
                }
                return Optional.of(new EquipSelection(
                        selectedWeapons.stream().map(Arma::getNombre).toList(),
                        selectedArmor == null ? "" : selectedArmor.getNombre()
                ));
            } catch (IllegalArgumentException ex) {
                Ui.messageDialog(parent, "Equipo activo", ex.getMessage());
            }
        }
    }

    private static void validateWeapons(List<Arma> weapons) {
        if (weapons.isEmpty()) {
            throw new IllegalArgumentException("Debes dejar al menos un arma activa.");
        }
        if (weapons.size() > 2) {
            throw new IllegalArgumentException("Solo puedes activar una o dos armas.");
        }
        long twoHanded = weapons.stream().filter(arma -> arma.getTipoMano() == TipoMano.DOS_MANOS).count();
        if (twoHanded > 1) {
            throw new IllegalArgumentException("No puedes activar dos armas de dos manos.");
        }
        if (twoHanded == 1 && weapons.size() > 1) {
            throw new IllegalArgumentException("Si activas un arma de dos manos, debe ser la unica arma activa.");
        }
    }

    private static void syncModel(DefaultListModel<Arma> model, List<Arma> values) {
        model.clear();
        for (Arma arma : values) {
            model.addElement(arma);
        }
    }

    private static void styleWeaponList(JList<Arma> list) {
        Ui.styleList(list);
        list.setVisibleRowCount(7);
        list.setCellRenderer((source, value, index, selected, focus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel(value == null ? "" : value.descripcion());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setBackground(selected ? new java.awt.Color(57, 69, 92) : Ui.INPUT);
            label.setForeground(Ui.TEXT);
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
            return label;
        });
    }

    private static void styleArmorList(JList<Armadura> list) {
        Ui.styleList(list);
        list.setVisibleRowCount(7);
        list.setCellRenderer((source, value, index, selected, focus) -> {
            javax.swing.JLabel label = new javax.swing.JLabel(value == null ? "" : value.descripcion());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setBackground(selected ? new java.awt.Color(57, 69, 92) : Ui.INPUT);
            label.setForeground(Ui.TEXT);
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
            return label;
        });
    }

    private static <T> List<T> values(DefaultListModel<T> model) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            result.add(model.get(i));
        }
        return result;
    }
}
