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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Scrollable;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

final class CharacterDialog extends JDialog {
    private TipoPersonaje selectedType = TipoPersonaje.VAMPIRO;
    private final JComboBox<TipoPersonaje> typeSelector = new JComboBox<>(TipoPersonaje.values());
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
    private final DefaultListModel<WeaponDraft> activeWeapons = new DefaultListModel<>();
    private final DefaultListModel<ArmorDraft> armors = new DefaultListModel<>();
    private final DefaultListModel<ModifierDraft> strengths = new DefaultListModel<>();
    private final DefaultListModel<ModifierDraft> weaknesses = new DefaultListModel<>();
    private final DefaultListModel<MinionDraft> minions = new DefaultListModel<>();
    private final JList<WeaponDraft> weaponList = new JList<>(weapons);
    private final JList<WeaponDraft> activeWeaponList = new JList<>(activeWeapons);
    private final JList<ArmorDraft> armorList = new JList<>(armors);
    private final JComboBox<ArmorDraft> activeArmor = new JComboBox<>();
    private final Visuals.PortraitPreviewPanel previewPanel = Visuals.portraitPreview();

    private ConfiguracionPersonaje result;

    private CharacterDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        installCompactInputs();
        bindPreview();
        setContentPane(buildContent());
        applyResponsiveSize();
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

        JTabbedPane tabs = Ui.tabbedPane();
        tabs.addTab("Base", scrollTab(buildBaseTab()));
        tabs.addTab("Equipo", scrollTab(buildEquipmentTab()));
        tabs.addTab("Rasgos", scrollTab(buildTraitsTab()));
        tabs.addTab("Esbirros", scrollTab(buildMinionsTab()));

        JButton save = Ui.primaryButton("Guardar personaje");
        JButton cancel = Ui.secondaryButton("Cancelar");
        save.addActionListener(event -> save());
        cancel.addActionListener(event -> dispose());
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(cancel);
        actions.add(save);

        JPanel root = Ui.dialogPanel(new BorderLayout(12, 12));
        root.add(header(tabs), BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(save);
        return root;
    }

    private JPanel header(JTabbedPane tabs) {
        JPanel banner = Ui.card();
        previewPanel.setPreferredSize(new Dimension(260, 136));
        previewPanel.setMinimumSize(new Dimension(240, 128));

        JPanel copy = Ui.transparent(new BorderLayout(0, 6));
        copy.add(Ui.small("Creacion de personaje"), BorderLayout.NORTH);
        copy.add(Ui.title("Forja de personaje", 28f), BorderLayout.CENTER);
        copy.add(Ui.label("Configura la raza, habilidad, rasgos, equipo y esbirros sin perder las reglas del dominio."),
                BorderLayout.SOUTH);

        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.add(Visuals.chipButton("Raza", Ui.GOLD, event -> {
            tabs.setSelectedIndex(0);
            typeSelector.requestFocusInWindow();
        }));
        actions.add(Visuals.chipButton("Habilidad", Ui.TEAL, event -> {
            tabs.setSelectedIndex(0);
            abilityName.requestFocusInWindow();
        }));
        actions.add(Visuals.chipButton("Equipo", Ui.BLUE, event -> tabs.setSelectedIndex(1)));
        actions.add(Visuals.chipButton("Rasgos", Ui.CRIMSON, event -> tabs.setSelectedIndex(2)));
        actions.add(Visuals.chipButton("Esbirros", Ui.VIOLET, event -> tabs.setSelectedIndex(3)));

        JPanel textColumn = Ui.transparent(new BorderLayout(0, 14));
        textColumn.add(copy, BorderLayout.CENTER);
        textColumn.add(actions, BorderLayout.SOUTH);

        JPanel previewWrap = Ui.transparent(new BorderLayout());
        previewWrap.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 18, 0, 0));
        previewWrap.add(previewPanel, BorderLayout.NORTH);

        banner.add(textColumn, BorderLayout.CENTER);
        banner.add(previewWrap, BorderLayout.EAST);
        return banner;
    }

    private JPanel buildBaseTab() {
        CardLayout raceLayout = new CardLayout();
        JPanel raceCards = Ui.transparent(raceLayout);
        raceCards.setPreferredSize(new Dimension(380, 220));
        raceCards.setMinimumSize(new Dimension(320, 198));
        raceCards.add(vampireForm(), TipoPersonaje.VAMPIRO.name());
        raceCards.add(wolfForm(), TipoPersonaje.LICANTROPO.name());
        raceCards.add(hunterForm(), TipoPersonaje.CAZADOR.name());
        raceCards.putClientProperty("layout", raceLayout);

        CardLayout abilityLayout = new CardLayout();
        JPanel abilityCards = Ui.transparent(abilityLayout);
        abilityCards.add(abilityExtra("Coste sangre", bloodCost), TipoPersonaje.VAMPIRO.name());
        abilityCards.add(wolfAbilityExtra(), TipoPersonaje.LICANTROPO.name());
        abilityCards.add(hunterAbilityExtra(), TipoPersonaje.CAZADOR.name());
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

    private JScrollPane scrollTab(Component content) {
        ViewportPanel viewportPanel = new ViewportPanel();
        viewportPanel.add(content, BorderLayout.CENTER);
        JScrollPane scroll = Ui.scroll(viewportPanel);
        scroll.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        scroll.getViewport().setOpaque(false);
        scroll.getViewport().setBackground(Ui.BG_2);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private void applyResponsiveSize() {
        java.awt.Rectangle bounds = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();
        int width = Math.max(920, Math.min(1120, bounds.width - 44));
        int height = Math.max(660, Math.min(740, bounds.height - 60));
        setSize(width, height);
        setMinimumSize(new java.awt.Dimension(Math.min(width, 920), Math.min(height, 660)));
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
        Ui.styleInput(typeSelector);
        typeSelector.setPreferredSize(new Dimension(214, 46));
        typeSelector.setMinimumSize(new Dimension(150, 44));
        activeArmor.setPreferredSize(new Dimension(240, 44));
        activeArmor.setMinimumSize(new Dimension(180, 42));
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
                "Usa sangre para activar disciplinas y dominar la arena con precision.",
                fieldBlock("Edad del vampiro", "Debe ser 1 o superior.", vampireAge),
                fieldBlock("Puntos de sangre iniciales", "Valor entre 0 y 10.", vampireBlood)
        );
    }

    private JPanel wolfForm() {
        return racePanel(
                "Licantropo",
                "Al transformarse aumenta altura, peso y presion fisica en combate.",
                fieldBlock("Incremento de altura en metros", "Valor entre 0.5 y 1.0.", wolfHeight),
                fieldBlock("Incremento de peso en kg", "Valor entre 90 y 110.", wolfWeight)
        );
    }

    private JPanel hunterForm() {
        return racePanel(
                "Cazador",
                "Voluntad sobrenatural inicial fijada por las reglas del juego.",
                fieldBlock("Voluntad inicial", "Valor fijo definido por las reglas del juego.", hunterWill)
        );
    }

    private JPanel racePanel(String title, String description, JPanel... fields) {
        JPanel panel = Ui.transparent(new BorderLayout(10, 10));
        JPanel heading = Ui.transparent(new BorderLayout(0, 2));
        JLabel titleLabel = Ui.title(title, 15f);
        titleLabel.setForeground(Ui.accentForType(selectedTypeFromTitle(title)));
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
        panel.setPreferredSize(new Dimension(320, 76));
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

    private JPanel hunterAbilityExtra() {
        JPanel panel = Ui.transparent(new BorderLayout(0, 8));
        panel.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new java.awt.Color(88, 103, 143), Ui.HUNTER, 14),
                javax.swing.BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        JLabel eyebrow = Ui.small("Talento activo");
        JLabel title = Ui.title("Cazador", 18f);
        title.setForeground(Ui.HUNTER);
        JLabel copy = Ui.small("<html>El talento del cazador no necesita parametros extra en esta fase de configuracion.</html>");
        copy.setForeground(Ui.MUTED);
        panel.add(eyebrow, BorderLayout.NORTH);
        JPanel center = Ui.transparent(new BorderLayout(0, 6));
        center.add(title, BorderLayout.NORTH);
        center.add(copy, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel wolfAbilityExtra() {
        CompactForm form = new CompactForm();
        form.addRow("Rabia minima", minRage);
        form.addRow("Uso del don", rageOnUse);
        return form;
    }

    private JPanel buildEquipmentTab() {
        setupEquipmentList(weaponList);
        setupEquipmentList(activeWeaponList);
        weaponList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        activeWeaponList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setupEquipmentList(armorList);
        Ui.styleInput(activeArmor);
        configureActiveArmorCombo();

        JPanel weaponsPanel = listPanel("Inventario de armas", weaponList,
                () -> DraftDialogs.weapon(this).ifPresent(draft -> {
                    weapons.addElement(draft);
                }),
                this::removeSelectedWeaponFromInventory);

        JButton activateWeapon = Ui.successButton("Activar");
        JButton deactivateWeapon = Ui.secondaryButton("Quitar activa");
        activateWeapon.addActionListener(event -> activateSelectedWeapon());
        deactivateWeapon.addActionListener(event -> deactivateSelectedWeapon());
        JPanel activeButtons = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        activeButtons.add(activateWeapon);
        activeButtons.add(deactivateWeapon);
        JPanel activeWeaponsBody = Ui.transparent(new BorderLayout(8, 8));
        activeWeaponsBody.add(listField(activeWeaponList, "Aun no has marcado armas activas."), BorderLayout.CENTER);
        activeWeaponsBody.add(activeButtons, BorderLayout.SOUTH);
        JPanel activeWeaponsPanel = Ui.titled("Armas activas", activeWeaponsBody);

        JPanel armorsPanel = listPanel("Inventario de armaduras", armorList,
                () -> DraftDialogs.armor(this).ifPresent(draft -> {
                    armors.addElement(draft);
                    refreshArmorCombo();
                }),
                this::removeSelectedArmorFromInventory);

        Ui.Form activeArmorForm = new Ui.Form();
        activeArmorForm.addRow("Armadura activa", activeArmor);
        JPanel activeArmorPanel = Ui.titled("Armadura activa", activeArmorForm);

        JPanel grid = Ui.transparent(new GridLayout(2, 2, 14, 14));
        grid.add(weaponsPanel);
        grid.add(activeWeaponsPanel);
        grid.add(armorsPanel);
        grid.add(activeArmorPanel);
        grid.setPreferredSize(new Dimension(0, 620));
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
        body.setPreferredSize(new Dimension(0, 220));
        body.add(listField(list, "Todavia no hay elementos en esta lista."), BorderLayout.CENTER);
        body.add(buttons, BorderLayout.SOUTH);
        return Ui.titled(title, body);
    }

    private JScrollPane listScroll(JList<?> list) {
        JScrollPane scroll = Ui.scroll(list);
        scroll.setPreferredSize(new Dimension(240, 170));
        scroll.setMinimumSize(new Dimension(200, 150));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        return scroll;
    }

    private JPanel listField(JList<?> list, String emptyText) {
        JScrollPane scroll = listScroll(list);
        JPanel empty = new JPanel(new BorderLayout());
        empty.setOpaque(true);
        empty.setBackground(Ui.INPUT);
        empty.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new java.awt.Color(68, 80, 115), Ui.TEAL, 12),
                javax.swing.BorderFactory.createEmptyBorder(0, 14, 0, 14)));
        empty.setPreferredSize(new Dimension(240, 170));
        empty.setMinimumSize(new Dimension(200, 150));
        JLabel label = Ui.small(emptyText);
        label.setHorizontalAlignment(JLabel.CENTER);
        empty.add(label, BorderLayout.CENTER);

        JPanel cards = Ui.transparent(new CardLayout());
        cards.setPreferredSize(new Dimension(260, 170));
        cards.setMinimumSize(new Dimension(220, 150));
        cards.add(empty, "empty");
        cards.add(scroll, "list");
        CardLayout layout = (CardLayout) cards.getLayout();
        Runnable refresh = () -> layout.show(cards, list.getModel().getSize() == 0 ? "empty" : "list");
        list.getModel().addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent event) {
                refresh.run();
            }

            @Override
            public void intervalRemoved(ListDataEvent event) {
                refresh.run();
            }

            @Override
            public void contentsChanged(ListDataEvent event) {
                refresh.run();
            }
        });
        refresh.run();
        return cards;
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
        for (WeaponDraft draft : values(activeWeapons)) {
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
        updatePreview();
    }

    private void setupTypeSelector() {
        typeSelector.setToolTipText("Seleccionar tipo de personaje");
        typeSelector.setSelectedItem(selectedType);
        typeSelector.addActionListener(event -> {
            TipoPersonaje choice = (TipoPersonaje) typeSelector.getSelectedItem();
            if (choice != null) {
                selectedType = choice;
                updateTypeCards();
            }
        });
        updatePreview();
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
        ArmorDraft previousSelection = (ArmorDraft) activeArmor.getSelectedItem();
        String previousName = previousSelection == null ? null : previousSelection.name();
        DefaultComboBoxModel<ArmorDraft> model = new DefaultComboBoxModel<>();
        for (ArmorDraft armor : values(armors)) {
            model.addElement(armor);
        }
        activeArmor.setModel(model);
        if (previousName == null) {
            activeArmor.setSelectedItem(null);
            return;
        }
        for (int i = 0; i < model.getSize(); i++) {
            ArmorDraft candidate = model.getElementAt(i);
            if (candidate.name().equalsIgnoreCase(previousName)) {
                activeArmor.setSelectedIndex(i);
                return;
            }
        }
        activeArmor.setSelectedItem(null);
    }

    private void activateSelectedWeapon() {
        WeaponDraft draft = weaponList.getSelectedValue();
        if (draft == null) {
            Ui.messageDialog(this, "Arma activa", "Selecciona primero un arma del inventario.");
            return;
        }
        try {
            List<WeaponDraft> selection = new ArrayList<>(values(activeWeapons));
            if (selection.stream().noneMatch(item -> item.name().equalsIgnoreCase(draft.name()))) {
                selection.add(draft);
            }
            validateActiveWeaponDrafts(selection);
            syncActiveWeapons(selection);
        } catch (IllegalArgumentException ex) {
            Ui.messageDialog(this, "Arma activa", ex.getMessage());
        }
    }

    private void deactivateSelectedWeapon() {
        WeaponDraft draft = activeWeaponList.getSelectedValue();
        if (draft == null) {
            Ui.messageDialog(this, "Arma activa", "Selecciona primero un arma activa para quitarla.");
            return;
        }
        List<WeaponDraft> selection = new ArrayList<>(values(activeWeapons));
        selection.removeIf(item -> item.name().equalsIgnoreCase(draft.name()));
        syncActiveWeapons(selection);
    }

    private void removeSelectedWeaponFromInventory() {
        WeaponDraft draft = weaponList.getSelectedValue();
        if (draft == null) {
            return;
        }
        for (int i = activeWeapons.size() - 1; i >= 0; i--) {
            if (activeWeapons.get(i).name().equalsIgnoreCase(draft.name())) {
                activeWeapons.remove(i);
            }
        }
        weapons.removeElement(draft);
    }

    private void removeSelectedArmorFromInventory() {
        ArmorDraft draft = armorList.getSelectedValue();
        if (draft == null) {
            return;
        }
        ArmorDraft current = (ArmorDraft) activeArmor.getSelectedItem();
        armors.removeElement(draft);
        refreshArmorCombo();
        if (current != null && current.name().equalsIgnoreCase(draft.name())) {
            activeArmor.setSelectedItem(null);
        }
    }

    private void validateActiveWeaponDrafts(List<WeaponDraft> drafts) {
        if (drafts.size() > 2) {
            throw new IllegalArgumentException("Solo puedes dejar una o dos armas activas.");
        }
        long twoHanded = drafts.stream().filter(item -> item.hand() == es.urjc.metprog.domain.equipment.TipoMano.DOS_MANOS).count();
        if (twoHanded > 1) {
            throw new IllegalArgumentException("No puedes activar dos armas de dos manos.");
        }
        if (twoHanded == 1 && drafts.size() > 1) {
            throw new IllegalArgumentException("Si activas un arma de dos manos, debe ser la unica arma activa.");
        }
    }

    private void syncActiveWeapons(List<WeaponDraft> selection) {
        activeWeapons.clear();
        for (WeaponDraft draft : selection) {
            activeWeapons.addElement(draft);
        }
    }

    private void bindPreview() {
        DocumentListener listener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent event) {
                updatePreview();
            }

            @Override
            public void removeUpdate(DocumentEvent event) {
                updatePreview();
            }

            @Override
            public void changedUpdate(DocumentEvent event) {
                updatePreview();
            }
        };
        name.getDocument().addDocumentListener(listener);
    }

    private void updatePreview() {
        String title = Ui.text(name);
        String copy = switch (selectedType) {
            case VAMPIRO -> "Edad, sangre y disciplina";
            case LICANTROPO -> "Altura, peso y transformacion";
            case CAZADOR -> "Voluntad y talento";
        };
        previewPanel.setPortrait(selectedType, title, copy);
    }

    private TipoPersonaje selectedTypeFromTitle(String title) {
        String normalized = title.toLowerCase();
        if (normalized.contains("vampi")) {
            return TipoPersonaje.VAMPIRO;
        }
        if (normalized.contains("lica")) {
            return TipoPersonaje.LICANTROPO;
        }
        return TipoPersonaje.CAZADOR;
    }

    private void configureActiveArmorCombo() {
        activeArmor.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null
                    ? "Selecciona una armadura del inventario."
                    : value.toString());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 12, 6, 12));
            label.setBackground(selected ? new java.awt.Color(55, 73, 116) : Ui.INPUT);
            label.setForeground(value == null ? Ui.MUTED : Ui.TEXT);
            label.setFont(label.getFont().deriveFont(index < 0 ? Font.BOLD : Font.PLAIN, 13f));
            return label;
        });
        activeArmor.setSelectedItem(null);
    }

    private static void setupEquipmentList(JList<?> list) {
        Ui.styleList(list);
        list.setVisibleRowCount(8);
        list.setCellRenderer((source, value, index, selected, focus) -> {
            JLabel label = new JLabel(value == null ? "" : value.toString());
            label.setOpaque(true);
            label.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 10, 8, 10));
            label.setBackground(selected ? new java.awt.Color(55, 73, 116) : Ui.INPUT);
            label.setForeground(Ui.TEXT);
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
            return label;
        });
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
        CompactForm() {
            super();
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }

        void addRow(String label, Component component) {
            JPanel row = Ui.transparent(new BorderLayout(14, 0));
            row.setBorder(javax.swing.BorderFactory.createEmptyBorder(6, 0, 6, 0));

            JLabel labelText = Ui.small(label);
            labelText.setPreferredSize(new Dimension(124, 24));
            row.add(labelText, BorderLayout.WEST);
            row.add(component, BorderLayout.CENTER);

            if (component instanceof javax.swing.JComponent jComponent) {
                Dimension preferred = jComponent.getPreferredSize();
                jComponent.setMaximumSize(new Dimension(Integer.MAX_VALUE, preferred.height));
            }
            add(row);
        }
    }

    private static final class ViewportPanel extends JPanel implements Scrollable {
        ViewportPanel() {
            super(new BorderLayout());
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return 18;
        }

        @Override
        public int getScrollableBlockIncrement(java.awt.Rectangle visibleRect, int orientation, int direction) {
            return Math.max(60, visibleRect.height - 40);
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
}
