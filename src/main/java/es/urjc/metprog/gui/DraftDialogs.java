package es.urjc.metprog.gui;

import es.urjc.metprog.domain.equipment.TipoMano;
import es.urjc.metprog.domain.minion.Lealtad;
import es.urjc.metprog.domain.modifier.TipoModificador;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class DraftDialogs {
    private DraftDialogs() {
    }

    static Optional<WeaponDraft> weapon(Component parent) {
        JTextField name = Ui.textField();
        JTextField attack = Ui.numberField(1);
        JComboBox<TipoMano> hand = combo(TipoMano.values());
        JCheckBox hasDefense = new JCheckBox("Anadir defensa extra");
        hasDefense.setOpaque(false);
        hasDefense.setForeground(Ui.TEXT);
        hasDefense.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        JTextField defense = Ui.numberField(1);

        Ui.Form form = new Ui.Form();
        form.addRow("Nombre", name);
        form.addRow("Ataque", attack);
        form.addRow("Mano", hand);
        form.addRow("Decorador", hasDefense);
        form.addRow("Defensa extra", defense);

        return confirmLoop(parent, "Nueva arma", form, () -> new WeaponDraft(
                required(name, "nombre del arma"),
                Ui.intValue(attack, 1, 3, "ataque del arma"),
                (TipoMano) hand.getSelectedItem(),
                hasDefense.isSelected(),
                Ui.intValue(defense, 1, 3, "defensa extra del arma")
        ));
    }

    static Optional<ArmorDraft> armor(Component parent) {
        JTextField name = Ui.textField();
        JTextField defense = Ui.numberField(1);
        JCheckBox hasAttack = new JCheckBox("Anadir ataque extra");
        hasAttack.setOpaque(false);
        hasAttack.setForeground(Ui.TEXT);
        hasAttack.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        JTextField attack = Ui.numberField(1);

        Ui.Form form = new Ui.Form();
        form.addRow("Nombre", name);
        form.addRow("Defensa", defense);
        form.addRow("Decorador", hasAttack);
        form.addRow("Ataque extra", attack);

        return confirmLoop(parent, "Nueva armadura", form, () -> new ArmorDraft(
                required(name, "nombre de la armadura"),
                Ui.intValue(defense, 1, 3, "defensa de la armadura"),
                hasAttack.isSelected(),
                Ui.intValue(attack, 1, 3, "ataque extra de la armadura")
        ));
    }

    static Optional<ModifierDraft> modifier(Component parent, TipoModificador fixedType) {
        JTextField name = Ui.textField();
        JTextField value = Ui.numberField(1);
        JComboBox<TipoModificador> type = combo(TipoModificador.values());
        type.setSelectedItem(fixedType);
        type.setEnabled(fixedType == null);

        Ui.Form form = new Ui.Form();
        form.addRow("Nombre", name);
        form.addRow("Valor", value);
        form.addRow("Tipo", type);

        return confirmLoop(parent, "Nuevo modificador", form, () -> new ModifierDraft(
                required(name, "nombre del modificador"),
                Ui.intValue(value, 1, 5, "valor del modificador"),
                (TipoModificador) type.getSelectedItem()
        ));
    }

    static Optional<MinionDraft> minion(Component parent) {
        Window owner = window(parent);
        JDialog dialog = new JDialog(owner, "Nuevo esbirro", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField name = Ui.textField();
        JTextField health = Ui.numberField(1);
        JComboBox<MinionKind> kind = combo(MinionKind.values());
        JComboBox<Lealtad> loyalty = combo(Lealtad.values());
        JTextField dependence = Ui.numberField(1);
        JTextField pact = Ui.textField();
        DefaultListModel<MinionDraft> childrenModel = new DefaultListModel<>();
        JList<MinionDraft> childrenList = new JList<>(childrenModel);
        childrenList.setVisibleRowCount(4);
        Ui.styleList(childrenList);
        compactMinionInput(name);
        compactMinionInput(health);
        compactMinionInput(dependence);
        compactMinionInput(pact);
        compactMinionCombo(kind);
        compactMinionCombo(loyalty);

        CardLayout extraLayout = new CardLayout();
        JPanel extra = Ui.transparent(extraLayout);

        Ui.Form human = new Ui.Form();
        human.addRow("Lealtad", loyalty);
        extra.add(human, MinionKind.HUMANO.name());

        Ui.Form ghoul = new Ui.Form();
        ghoul.addRow("Dependencia", dependence);
        extra.add(ghoul, MinionKind.GHOUL.name());

        JPanel demon = Ui.transparent(new BorderLayout(8, 8));
        Ui.Form demonForm = new Ui.Form();
        demonForm.addRow("Pacto", pact);
        demon.add(demonForm, BorderLayout.NORTH);
        JPanel childButtons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton addChild = Ui.secondaryButton("Anadir subordinado");
        JButton removeChild = Ui.dangerButton("Quitar");
        childButtons.add(addChild);
        childButtons.add(removeChild);
        demon.add(Ui.scroll(childrenList), BorderLayout.CENTER);
        demon.add(childButtons, BorderLayout.SOUTH);
        extra.add(demon, MinionKind.DEMONIO.name());

        kind.addActionListener(event -> extraLayout.show(extra, ((MinionKind) kind.getSelectedItem()).name()));
        addChild.addActionListener(event -> minion(dialog).ifPresent(childrenModel::addElement));
        removeChild.addActionListener(event -> {
            int index = childrenList.getSelectedIndex();
            if (index >= 0) {
                childrenModel.remove(index);
            }
        });

        Ui.Form base = new Ui.Form();
        base.addRow("Tipo", kind);
        base.addRow("Nombre", name);
        base.addRow("Salud", health);

        JPanel body = Ui.card();
        body.add(base, BorderLayout.NORTH);
        body.add(extra, BorderLayout.CENTER);

        JButton ok = Ui.primaryButton("Crear");
        JButton cancel = Ui.secondaryButton("Cancelar");
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(cancel);
        actions.add(ok);

        JPanel root = Ui.dialogPanel(new BorderLayout(12, 12));
        root.add(body, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.setSize(620, 620);
        dialog.setMinimumSize(new Dimension(560, 560));
        dialog.setLocationRelativeTo(owner);

        final MinionDraft[] result = new MinionDraft[1];
        ok.addActionListener(event -> {
            try {
                MinionKind selectedKind = (MinionKind) kind.getSelectedItem();
                result[0] = new MinionDraft(
                        selectedKind,
                        required(name, "nombre del esbirro"),
                        Ui.intValue(health, 1, 3, "salud del esbirro"),
                        (Lealtad) loyalty.getSelectedItem(),
                        Ui.intValue(dependence, 1, 5, "dependencia del ghoul"),
                        selectedKind == MinionKind.DEMONIO ? required(pact, "pacto del demonio") : "",
                        values(childrenModel)
                );
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                Ui.messageDialog(dialog, "Dato no valido", ex.getMessage());
            }
        });
        cancel.addActionListener(event -> dialog.dispose());
        extraLayout.show(extra, MinionKind.HUMANO.name());
        dialog.setVisible(true);
        return Optional.ofNullable(result[0]);
    }

    private static void compactMinionInput(JTextField field) {
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(Ui.LINE, Ui.TEAL, 8),
                javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        field.setPreferredSize(new Dimension(180, 38));
        field.setMinimumSize(new Dimension(90, 34));
    }

    private static void compactMinionCombo(JComboBox<?> combo) {
        combo.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(Ui.LINE, Ui.TEAL, 8),
                javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        combo.setPreferredSize(new Dimension(180, 42));
        combo.setMinimumSize(new Dimension(140, 38));
    }

    static Optional<List<String>> modifierSelection(Component parent, String title, List<String> available) {
        DefaultListModel<String> model = new DefaultListModel<>();
        available.forEach(model::addElement);
        JList<String> list = new JList<>(model);
        list.setVisibleRowCount(8);
        Ui.styleList(list);
        list.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(Ui.scroll(list), BorderLayout.CENTER);
        if (!Ui.confirmDialog(parent, title, root)) {
            return Optional.empty();
        }
        return Optional.of(list.getSelectedValuesList());
    }

    private static <T> JComboBox<T> combo(T[] values) {
        JComboBox<T> combo = new JComboBox<>(new DefaultComboBoxModel<>(values));
        Ui.styleInput(combo);
        return combo;
    }

    private static <T> Optional<T> confirmLoop(Component parent, String title, JComponent body, Factory<T> factory) {
        JPanel root = Ui.transparent(new BorderLayout(12, 12));
        root.add(body, BorderLayout.CENTER);
        while (true) {
            if (!Ui.confirmDialog(parent, title, root)) {
                return Optional.empty();
            }
            try {
                return Optional.of(factory.create());
            } catch (RuntimeException ex) {
                Ui.messageDialog(parent, "Dato no valido", ex.getMessage());
            }
        }
    }

    private static String required(JTextField field, String name) {
        String text = Ui.text(field);
        if (text.isBlank()) {
            throw new IllegalArgumentException("El campo " + name + " es obligatorio.");
        }
        return text;
    }

    private static <T> List<T> values(DefaultListModel<T> model) {
        List<T> values = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            values.add(model.get(i));
        }
        return values;
    }

    private static Window window(Component component) {
        Window window = SwingUtilities.getWindowAncestor(component);
        return window == null ? JOptionPane.getRootFrame() : window;
    }

    @FunctionalInterface
    private interface Factory<T> {
        T create();
    }
}
