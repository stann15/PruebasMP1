package es.urjc.metprog.gui;

import es.urjc.metprog.domain.ability.Disciplina;
import es.urjc.metprog.domain.ability.Don;
import es.urjc.metprog.domain.ability.HabilidadEspecial;
import es.urjc.metprog.domain.ability.Talento;
import es.urjc.metprog.domain.character.ConfiguracionPersonaje;
import es.urjc.metprog.domain.character.TipoPersonaje;
import es.urjc.metprog.domain.modifier.TipoModificador;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class CharacterDialog extends JDialog {
    private TipoPersonaje selectedType = TipoPersonaje.VAMPIRO;
    private final JTextField typeSelector = Ui.textField();
    private final JPopupMenu typeMenu = new JPopupMenu();
    private final JTextField name = Ui.textField();
    private final JTextField power = Ui.numberField(3);
    private final JTextField gold = Ui.numberField(30);
    private final JTextField abilityName = Ui.textField();
    private final JTextField abilityAttack = Ui.numberField(1);
    private final JTextField abilityDefense = Ui.numberField(1);
    private final JTextField bloodCost = Ui.numberField(1);
    private final JTextField minRage = Ui.numberField(0);
    private final JCheckBox rageOnUse = new JCheckBox("Incrementa rabia al usarse");
    private final JTextField vampireAge = Ui.numberField(100);
    private final JTextField vampireBlood = Ui.numberField(5);
    private final JTextField wolfHeight = Ui.decimalField(0.8);
    private final JTextField wolfWeight = Ui.numberField(100);
    private final JTextField hunterWill = Ui.numberField(3);

    private final DefaultListModel<WeaponDraft> weapons = new DefaultListModel<>();
    private final DefaultListModel<ArmorDraft> armors = new DefaultListModel<>();
    private final DefaultListModel<ModifierDraft> strengths = new DefaultListModel<>();
    private final DefaultListModel<ModifierDraft> weaknesses = new DefaultListModel<>();
    private final DefaultListModel<MinionDraft> minions = new DefaultListModel<>();
    private final JList<WeaponDraft> weaponList = new JList<>(weapons);
    private final JList<WeaponDraft> activeWeaponList = new JList<>(weapons);
    private final JList<ArmorDraft> armorList = new JList<>(armors);
    private final JComboBox<ArmorDraft> activeArmor = new JComboBox<>();

    private ConfiguracionPersonaje result;

    private CharacterDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        installCompactInputs();
        setContentPane(buildContent());
        setSize(1040, 740);
        setMinimumSize(new java.awt.Dimension(900, 660));
        setLocationRelativeTo(owner);
        updateTypeCards();
    }

    static Optional<ConfiguracionPersonaje> show(Component parent, String title) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        CharacterDialog dialog = new CharacterDialog(owner == null ? JOptionPane.getRootFrame() : owner, title);
        dialog.setVisible(true);
        return Optional.ofNullable(dialog.result);
    }

    private JPanel buildContent() {
        setupTypeSelector();
        rageOnUse.setOpaque(false);
        rageOnUse.setForeground(Ui.TEXT);
        hunterWill.setEditable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Ui.PANEL);
        tabs.setForeground(Ui.TEXT);
        tabs.setOpaque(false);
        tabs.setFocusable(false);
        tabs.addTab("Base", buildBaseTab());
        tabs.addTab("Equipo", buildEquipmentTab());
        tabs.addTab("Rasgos", buildTraitsTab());
        tabs.addTab("Esbirros", buildMinionsTab());

        JButton save = Ui.primaryButton("Guardar personaje");
        JButton cancel = Ui.secondaryButton("Cancelar");
        save.addActionListener(event -> save());
        cancel.addActionListener(event -> dispose());
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(cancel);
        actions.add(save);

        JPanel root = Ui.dialogPanel(new BorderLayout(12, 12));
        root.add(header(), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);
        return root;
    }

    private JPanel header() {
        JPanel header = Ui.transparent(new BorderLayout(8, 4));
        header.add(Ui.title("Forja de personaje", 24f), BorderLayout.NORTH);
        header.add(Ui.small("Los datos se validan con las mismas reglas del dominio."), BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildBaseTab() {
        CardLayout raceLayout = new CardLayout();
        JPanel raceCards = Ui.transparent(raceLayout);
        raceCards.setPreferredSize(new Dimension(420, 210));
        raceCards.setMinimumSize(new Dimension(360, 190));
        raceCards.add(vampireForm(), TipoPersonaje.VAMPIRO.name());
        raceCards.add(wolfForm(), TipoPersonaje.LICANTROPO.name());
        raceCards.add(hunterForm(), TipoPersonaje.CAZADOR.name());
        raceCards.putClientProperty("layout", raceLayout);

        CardLayout abilityLayout = new CardLayout();
        JPanel abilityCards = Ui.transparent(abilityLayout);
        abilityCards.add(abilityExtra("Coste sangre", bloodCost), TipoPersonaje.VAMPIRO.name());
        abilityCards.add(wolfAbilityExtra(), TipoPersonaje.LICANTROPO.name());
        abilityCards.add(Ui.small("El talento no necesita campos adicionales."), TipoPersonaje.CAZADOR.name());
        abilityCards.putClientProperty("layout", abilityLayout);

        CompactForm base = new CompactForm();
        base.addRow("Tipo", typeSelector);
        base.addRow("Nombre", name);
        base.addRow("Poder", power);
        base.addRow("Oro inicial", gold);

        CompactForm ability = new CompactForm();
        ability.addRow("Habilidad", abilityName);
        ability.addRow("Ataque habilidad", abilityAttack);
        ability.addRow("Defensa habilidad", abilityDefense);

        JPanel identityCard = Ui.titled("Identidad", base);
        JPanel abilityCard = Ui.titled("Habilidad especial", ability);
        JPanel raceCard = Ui.titled("Rasgo de raza", raceCards);
        JPanel abilitySettingsCard = Ui.titled("Ajustes de habilidad", abilityCards);

        JPanel grid = Ui.transparent(new GridBagLayout());
        addBaseCard(grid, identityCard, 0, 0, 0.53, new Insets(0, 0, 14, 14));
        addBaseCard(grid, abilityCard, 1, 0, 0.53, new Insets(0, 0, 14, 0));
        addBaseCard(grid, raceCard, 0, 1, 0.47, new Insets(0, 0, 0, 14));
        addBaseCard(grid, abilitySettingsCard, 1, 1, 0.47, new Insets(0, 0, 0, 0));

        JPanel tab = Ui.transparent(new BorderLayout(14, 14));
        tab.add(grid, BorderLayout.CENTER);
        return tab;
    }

    private void addBaseCard(JPanel grid, JPanel card, int x, int y, double weightY, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weightx = 0.5;
        constraints.weighty = weightY;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = insets;
        grid.add(card, constraints);
    }

    private void installCompactInputs() {
        JTextField[] compactFields = {
                typeSelector,
                name,
                power,
                gold,
                abilityName,
                abilityAttack,
                abilityDefense,
                bloodCost,
                minRage,
                vampireAge,
                vampireBlood,
                wolfHeight,
                wolfWeight,
                hunterWill
        };
        for (JTextField field : compactFields) {
            compactInput(field);
        }
        rageOnUse.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 0, 4, 0));
        rageOnUse.setPreferredSize(new Dimension(220, 32));
        rageOnUse.setMinimumSize(new Dimension(180, 30));
    }

    private void compactInput(JTextField field) {
        field.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(Ui.LINE, Ui.TEAL, 8),
                javax.swing.BorderFactory.createEmptyBorder(2, 10, 2, 10)));
        field.setPreferredSize(new Dimension(180, 38));
        field.setMinimumSize(new Dimension(90, 34));
    }

    private JPanel vampireForm() {
        return racePanel(
                "Vampiro",
                "Usa sangre para activar disciplinas.",
                fieldBlock("Edad del vampiro", "Debe ser 1 o superior.", vampireAge),
                fieldBlock("Puntos de sangre iniciales", "Valor entre 0 y 10.", vampireBlood)
        );
    }

    private JPanel wolfForm() {
        return racePanel(
                "Licantropo",
                "Al transformarse aumenta altura y peso.",
                fieldBlock("Incremento de altura en metros", "Valor entre 0.5 y 1.0.", wolfHeight),
                fieldBlock("Incremento de peso en kg", "Valor entre 90 y 110.", wolfWeight)
        );
    }

    private JPanel hunterForm() {
        return racePanel(
                "Cazador",
                "Voluntad sobrenatural inicial fijada por las reglas.",
                fieldBlock("Voluntad inicial", "Valor fijo definido por las reglas del juego.", hunterWill)
        );
    }

    private JPanel racePanel(String title, String description, JPanel... fields) {
        JPanel panel = Ui.transparent(new BorderLayout(10, 10));
        JPanel heading = Ui.transparent(new BorderLayout(0, 2));
        JLabel titleLabel = Ui.title(title, 15f);
        titleLabel.setForeground(Ui.GOLD);
        heading.add(titleLabel, BorderLayout.NORTH);
        heading.add(Ui.small(description), BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);

        JPanel rows = Ui.transparent(new GridLayout(fields.length, 1, 0, 8));
        for (JPanel field : fields) {
            rows.add(field);
        }
        panel.add(rows, BorderLayout.CENTER);
        return panel;
    }

    private JPanel fieldBlock(String label, String hint, JTextField field) {
        JPanel panel = Ui.transparent(new BorderLayout(0, 5));
        panel.setPreferredSize(new Dimension(360, 68));
        JLabel labelText = Ui.label(label);
        labelText.setForeground(Ui.TEXT);
        JPanel copy = Ui.transparent(new BorderLayout(10, 0));
        copy.add(labelText, BorderLayout.WEST);
        copy.add(Ui.small(hint), BorderLayout.EAST);
        panel.add(copy, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JPanel abilityExtra(String label, Component field) {
        CompactForm form = new CompactForm();
        form.addRow(label, field);
        return form;
    }

    private JPanel wolfAbilityExtra() {
        CompactForm form = new CompactForm();
        form.addRow("Rabia minima", minRage);
        form.addRow("Uso del don", rageOnUse);
        return form;
    }

    private JPanel buildEquipmentTab() {
        setupList(weaponList);
        setupList(activeWeaponList);
        activeWeaponList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setupList(armorList);
        Ui.styleInput(activeArmor);

        JPanel weaponsPanel = listPanel("Inventario de armas", weaponList,
                () -> DraftDialogs.weapon(this).ifPresent(draft -> {
                    weapons.addElement(draft);
                    selectLast(activeWeaponList, weapons.size());
                }),
                () -> removeSelected(weaponList, weapons));

        JPanel activeWeaponsPanel = Ui.titled("Armas activas", Ui.scroll(activeWeaponList));

        JPanel armorsPanel = listPanel("Inventario de armaduras", armorList,
                () -> DraftDialogs.armor(this).ifPresent(draft -> {
                    armors.addElement(draft);
                    refreshArmorCombo();
                }),
                () -> {
                    removeSelected(armorList, armors);
                    refreshArmorCombo();
                });

        Ui.Form activeArmorForm = new Ui.Form();
        activeArmorForm.addRow("Armadura activa", activeArmor);
        JPanel activeArmorPanel = Ui.titled("Armadura activa", activeArmorForm);

        JPanel grid = Ui.transparent(new GridLayout(2, 2, 14, 14));
        grid.add(weaponsPanel);
        grid.add(activeWeaponsPanel);
        grid.add(armorsPanel);
        grid.add(activeArmorPanel);
        return grid;
    }

    private JPanel buildTraitsTab() {
        JList<ModifierDraft> strengthList = new JList<>(strengths);
        JList<ModifierDraft> weaknessList = new JList<>(weaknesses);
        setupList(strengthList);
        setupList(weaknessList);
        JPanel grid = Ui.transparent(new GridLayout(1, 2, 14, 14));
        grid.add(listPanel("Fortalezas", strengthList,
                () -> DraftDialogs.modifier(this, TipoModificador.FORTALEZA).ifPresent(strengths::addElement),
                () -> removeSelected(strengthList, strengths)));
        grid.add(listPanel("Debilidades", weaknessList,
                () -> DraftDialogs.modifier(this, TipoModificador.DEBILIDAD).ifPresent(weaknesses::addElement),
                () -> removeSelected(weaknessList, weaknesses)));
        return grid;
    }

    private JPanel buildMinionsTab() {
        JList<MinionDraft> minionList = new JList<>(minions);
        setupList(minionList);
        return listPanel("Esbirros", minionList,
                () -> DraftDialogs.minion(this).ifPresent(minions::addElement),
                () -> removeSelected(minionList, minions));
    }

    private JPanel listPanel(String title, JList<?> list, Runnable add, Runnable remove) {
        JButton addButton = Ui.secondaryButton("Anadir");
        JButton removeButton = Ui.dangerButton("Quitar");
        addButton.addActionListener(event -> add.run());
        removeButton.addActionListener(event -> remove.run());
        JPanel buttons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.add(addButton);
        buttons.add(removeButton);
        JPanel body = Ui.transparent(new BorderLayout(8, 8));
        body.add(Ui.scroll(list), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);
        return Ui.titled(title, body);
    }

    private void save() {
        try {
            ConfiguracionPersonaje config = buildConfig();
            validateEquipmentSelection(config);
            result = config;
            dispose();
        } catch (RuntimeException ex) {
            Ui.messageDialog(this, "No se puede crear el personaje", ex.getMessage());
        }
    }

    private ConfiguracionPersonaje buildConfig() {
        ConfiguracionPersonaje config = new ConfiguracionPersonaje();
        config.setTipo(selectedType);
        config.setNombre(required(name, "nombre del personaje"));
        config.setPoder(Ui.intValue(power, 1, 5, "poder"));
        config.setOro(Ui.intValue(gold, 0, 999999, "oro inicial"));
        config.setHabilidadEspecial(buildAbility(selectedType));

        if (selectedType == TipoPersonaje.VAMPIRO) {
            config.setEdadVampiro(Ui.intValue(vampireAge, 1, 9999, "edad"));
            config.setSangreInicial(Ui.intValue(vampireBlood, 0, 10, "sangre inicial"));
        } else if (selectedType == TipoPersonaje.LICANTROPO) {
            config.setIncrementoAltura(Ui.doubleValue(wolfHeight, 0.5, 1.0, "incremento de altura"));
            config.setIncrementoPeso(Ui.intValue(wolfWeight, 90, 110, "incremento de peso"));
        }

        for (WeaponDraft draft : values(weapons)) {
            config.getArmas().add(draft.build());
        }
        for (ArmorDraft draft : values(armors)) {
            config.getArmaduras().add(draft.build());
        }
        for (ModifierDraft draft : values(strengths)) {
            config.getFortalezas().add(draft.build());
        }
        for (ModifierDraft draft : values(weaknesses)) {
            config.getDebilidades().add(draft.build());
        }
        for (MinionDraft draft : values(minions)) {
            config.getEsbirros().add(draft.build());
        }
        for (WeaponDraft draft : activeWeaponList.getSelectedValuesList()) {
            config.getNombresArmasActivas().add(draft.name());
        }
        ArmorDraft selectedArmor = (ArmorDraft) activeArmor.getSelectedItem();
        if (selectedArmor != null) {
            config.setNombreArmaduraActiva(selectedArmor.name());
        }
        return config;
    }

    private HabilidadEspecial buildAbility(TipoPersonaje selectedType) {
        String ability = required(abilityName, "nombre de la habilidad");
        int attack = Ui.intValue(abilityAttack, 1, 3, "ataque de habilidad");
        int defense = Ui.intValue(abilityDefense, 1, 3, "defensa de habilidad");
        return switch (selectedType) {
            case VAMPIRO -> new Disciplina(ability, attack, defense, Ui.intValue(bloodCost, 1, 3, "coste de sangre"));
            case LICANTROPO -> new Don(ability, attack, defense, Ui.intValue(minRage, 0, 3, "rabia minima"), rageOnUse.isSelected());
            case CAZADOR -> new Talento(ability, attack, defense);
        };
    }

    private void validateEquipmentSelection(ConfiguracionPersonaje config) {
        if (!config.getArmas().isEmpty() && config.getNombresArmasActivas().isEmpty()) {
            throw new IllegalArgumentException("Selecciona al menos un arma activa.");
        }
        if (!config.getArmaduras().isEmpty() && (config.getNombreArmaduraActiva() == null || config.getNombreArmaduraActiva().isBlank())) {
            throw new IllegalArgumentException("Selecciona una armadura activa.");
        }
    }

    private void updateTypeCards() {
        updateCards(this, selectedType.name());
    }

    private void setupTypeSelector() {
        if (typeMenu.getComponentCount() > 0) {
            return;
        }
        typeSelector.setEditable(false);
        typeSelector.setFocusable(false);
        typeSelector.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        typeSelector.setToolTipText("Seleccionar tipo de personaje");
        typeMenu.setBackground(Ui.INPUT);
        typeMenu.setBorder(javax.swing.BorderFactory.createLineBorder(Ui.LINE));

        for (TipoPersonaje option : TipoPersonaje.values()) {
            JMenuItem item = new JMenuItem(typeText(option));
            item.setOpaque(true);
            item.setBackground(Ui.INPUT);
            item.setForeground(Ui.TEXT);
            item.setFont(item.getFont().deriveFont(java.awt.Font.BOLD, 14f));
            item.addActionListener(event -> {
                selectedType = option;
                updateTypeSelectorText();
                updateTypeCards();
            });
            typeMenu.add(item);
        }

        typeSelector.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent event) {
                typeMenu.show(typeSelector, 0, typeSelector.getHeight());
            }
        });
        updateTypeSelectorText();
    }

    private void updateTypeSelectorText() {
        typeSelector.setText(typeText(selectedType) + "    v");
    }

    private String typeText(TipoPersonaje option) {
        return switch (option) {
            case VAMPIRO -> "Vampiro";
            case LICANTROPO -> "Licantropo";
            case CAZADOR -> "Cazador";
        };
    }

    private void updateCards(Component component, String name) {
        if (component instanceof JPanel panel && panel.getClientProperty("layout") instanceof CardLayout layout) {
            layout.show(panel, name);
        }
        if (component instanceof java.awt.Container container) {
            for (Component child : container.getComponents()) {
                updateCards(child, name);
            }
        }
    }

    private void refreshArmorCombo() {
        DefaultComboBoxModel<ArmorDraft> model = new DefaultComboBoxModel<>();
        for (ArmorDraft armor : values(armors)) {
            model.addElement(armor);
        }
        activeArmor.setModel(model);
    }

    private static void setupList(JList<?> list) {
        Ui.styleList(list);
        list.setVisibleRowCount(8);
    }

    private static <T> void removeSelected(JList<T> list, DefaultListModel<T> model) {
        int index = list.getSelectedIndex();
        if (index >= 0) {
            model.remove(index);
        }
    }

    private static void selectLast(JList<?> list, int size) {
        if (size > 0) {
            list.addSelectionInterval(size - 1, size - 1);
        }
    }

    private static String required(JTextField field, String label) {
        String text = Ui.text(field);
        if (text.isBlank()) {
            throw new IllegalArgumentException("El campo " + label + " es obligatorio.");
        }
        return text;
    }

    private static <T> List<T> values(DefaultListModel<T> model) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            result.add(model.get(i));
        }
        return result;
    }

    private static final class CompactForm extends JPanel {
        private int row;

        CompactForm() {
            super(new GridBagLayout());
            setOpaque(false);
        }

        void addRow(String label, Component component) {
            GridBagConstraints left = new GridBagConstraints();
            left.gridx = 0;
            left.gridy = row;
            left.anchor = GridBagConstraints.WEST;
            left.insets = new Insets(5, 0, 5, 12);
            add(Ui.small(label), left);

            GridBagConstraints right = new GridBagConstraints();
            right.gridx = 1;
            right.gridy = row;
            right.weightx = 1;
            right.fill = GridBagConstraints.HORIZONTAL;
            right.insets = new Insets(5, 0, 5, 0);
            add(component, right);
            row++;
        }
    }
}
