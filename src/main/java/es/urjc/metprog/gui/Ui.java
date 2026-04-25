package es.urjc.metprog.gui;

import es.urjc.metprog.domain.character.TipoPersonaje;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.JTextComponent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.geom.RoundRectangle2D;
import java.text.ParseException;

final class Ui {
    static final Color BG = new Color(7, 9, 16);
    static final Color BG_2 = new Color(13, 17, 29);
    static final Color PANEL = new Color(24, 29, 45);
    static final Color PANEL_ALT = new Color(35, 42, 64);
    static final Color INPUT = new Color(11, 15, 27);
    static final Color LINE = new Color(96, 111, 151);
    static final Color TEXT = new Color(248, 248, 241);
    static final Color MUTED = new Color(180, 188, 205);
    static final Color GOLD = new Color(255, 194, 85);
    static final Color CRIMSON = new Color(232, 72, 101);
    static final Color TEAL = new Color(58, 223, 201);
    static final Color GREEN = new Color(99, 222, 135);
    static final Color BLUE = new Color(95, 154, 255);
    static final Color VIOLET = new Color(166, 105, 255);
    static final Color VAMPIRE = new Color(232, 72, 101);
    static final Color WOLF = new Color(255, 176, 77);
    static final Color HUNTER = new Color(73, 221, 216);

    private Ui() {
    }

    static void install() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        ToolTipManager.sharedInstance().setInitialDelay(250);

        UIManager.put("Panel.background", BG);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("OptionPane.background", PANEL);
        UIManager.put("OptionPane.messageForeground", TEXT);
        UIManager.put("Button.background", PANEL_ALT);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("Button.focus", new Color(0, 0, 0, 0));
        UIManager.put("Button.select", new Color(49, 63, 99));
        UIManager.put("Button.disabledText", new Color(116, 124, 148));
        UIManager.put("TextField.background", INPUT);
        UIManager.put("TextField.foreground", TEXT);
        UIManager.put("TextField.caretForeground", GOLD);
        UIManager.put("FormattedTextField.background", INPUT);
        UIManager.put("FormattedTextField.foreground", TEXT);
        UIManager.put("FormattedTextField.caretForeground", GOLD);
        UIManager.put("PasswordField.background", INPUT);
        UIManager.put("PasswordField.foreground", TEXT);
        UIManager.put("PasswordField.caretForeground", GOLD);
        UIManager.put("TextArea.background", INPUT);
        UIManager.put("TextArea.foreground", TEXT);
        UIManager.put("TextArea.caretForeground", GOLD);
        UIManager.put("ComboBox.background", INPUT);
        UIManager.put("ComboBox.foreground", TEXT);
        UIManager.put("ComboBox.buttonBackground", new Color(24, 31, 49));
        UIManager.put("ComboBox.buttonDarkShadow", LINE);
        UIManager.put("ComboBox.buttonHighlight", new Color(92, 112, 157));
        UIManager.put("ComboBox.buttonShadow", new Color(29, 37, 58));
        UIManager.put("ComboBox.selectionBackground", new Color(50, 67, 104));
        UIManager.put("ComboBox.selectionForeground", TEXT);
        UIManager.put("Spinner.background", INPUT);
        UIManager.put("Spinner.foreground", TEXT);
        UIManager.put("Spinner.border", BorderFactory.createLineBorder(LINE));
        UIManager.put("List.background", INPUT);
        UIManager.put("List.foreground", TEXT);
        UIManager.put("List.selectionBackground", new Color(54, 70, 108));
        UIManager.put("List.selectionForeground", TEXT);
        UIManager.put("Table.background", INPUT);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", new Color(54, 70, 108));
        UIManager.put("Table.selectionForeground", TEXT);
        UIManager.put("TabbedPane.background", PANEL);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selected", new Color(40, 52, 82));
        UIManager.put("TabbedPane.contentAreaColor", new Color(40, 52, 82));
        UIManager.put("TabbedPane.tabAreaBackground", BG_2);
        UIManager.put("TabbedPane.focus", TEAL);
        UIManager.put("TabbedPane.darkShadow", BG);
        UIManager.put("TabbedPane.light", BG_2);
        UIManager.put("TabbedPane.highlight", TEAL);
        UIManager.put("CheckBox.background", PANEL);
        UIManager.put("CheckBox.foreground", TEXT);
    }

    static JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 14f));
        return label;
    }

    static JLabel small(String text) {
        JLabel label = label(text);
        label.setForeground(MUTED);
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        return label;
    }

    static JLabel title(String text, float size) {
        JLabel label = label(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, size));
        label.setForeground(TEXT);
        return label;
    }

    static String typeText(TipoPersonaje type) {
        return switch (type) {
            case VAMPIRO -> "Vampiro";
            case LICANTROPO -> "Licantropo";
            case CAZADOR -> "Cazador";
        };
    }

    static Color accentForType(TipoPersonaje type) {
        return switch (type) {
            case VAMPIRO -> VAMPIRE;
            case LICANTROPO -> WOLF;
            case CAZADOR -> HUNTER;
        };
    }

    static Color softAccentForType(TipoPersonaje type) {
        Color base = accentForType(type);
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), 72);
    }

    static JTextField textField() {
        JTextField field = new JTextField();
        styleInput(field);
        return field;
    }

    static JPasswordField passwordField() {
        JPasswordField field = new JPasswordField();
        styleInput(field);
        return field;
    }

    static JTextField numberField(int value) {
        JTextField field = textField();
        field.setText(String.valueOf(value));
        field.setHorizontalAlignment(JTextField.RIGHT);
        return field;
    }

    static JTextField decimalField(double value) {
        JTextField field = textField();
        field.setText(String.valueOf(value));
        field.setHorizontalAlignment(JTextField.RIGHT);
        return field;
    }

    static JTextArea textArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBackground(INPUT);
        area.setForeground(TEXT);
        area.setCaretColor(GOLD);
        area.setSelectionColor(new Color(73, 88, 133));
        area.setSelectedTextColor(TEXT);
        area.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    static void styleInput(JComponent component) {
        component.setBackground(INPUT);
        component.setForeground(TEXT);
        component.setBorder(BorderFactory.createCompoundBorder(new GlowBorder(LINE, TEAL, 9), BorderFactory.createEmptyBorder(16, 20, 16, 20)));
        component.setFont(component.getFont().deriveFont(Font.PLAIN, 14f));
        component.setCursor(Cursor.getPredefinedCursor(component instanceof JTextComponent ? Cursor.TEXT_CURSOR : Cursor.DEFAULT_CURSOR));
        if (component instanceof JTextComponent text) {
            styleText(text);
        } else if (component instanceof JSpinner spinner) {
            styleSpinner(spinner);
        } else if (component instanceof JComboBox<?> combo) {
            styleCombo(combo);
        }
    }

    static JSpinner spinner(int value, int min, int max, int step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        styleInput(spinner);
        return spinner;
    }

    static JSpinner decimalSpinner(double value, double min, double max, double step) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        styleInput(spinner);
        return spinner;
    }

    static JButton primaryButton(String text) {
        return button(text, GOLD, new Color(255, 119, 82), new Color(40, 26, 8));
    }

    static JButton secondaryButton(String text) {
        return button(text, BLUE, VIOLET, TEXT);
    }

    static JButton dangerButton(String text) {
        return button(text, CRIMSON, new Color(255, 132, 99), TEXT);
    }

    static JButton successButton(String text) {
        return button(text, TEAL, GREEN, new Color(4, 22, 24));
    }

    static JButton ghostButton(String text) {
        JButton button = new GlowButton(text, new Color(21, 26, 41), new Color(28, 34, 54), TEXT);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setMargin(new Insets(8, 12, 8, 12));
        return button;
    }

    static JButton button(String text, Color start, Color end, Color fg) {
        JButton button = new GlowButton(text, start, end, fg);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
        button.setMargin(new Insets(10, 15, 10, 15));
        button.setToolTipText(text);
        return button;
    }

    static void setSpeedSelected(JButton button, boolean selected) {
        button.putClientProperty("speedSelected", selected);
        button.repaint();
    }

    static JPanel row(Component... components) {
        JPanel panel = transparent(new FlowLayout(FlowLayout.LEFT, 10, 0));
        for (Component component : components) {
            panel.add(component);
        }
        return panel;
    }

    static JPanel transparent(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    static JTabbedPane tabbedPane() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(PANEL);
        tabs.setForeground(TEXT);
        tabs.setOpaque(false);
        tabs.setFocusable(false);
        tabs.setUI(tabbedPaneUi());
        return tabs;
    }

    static BasicTabbedPaneUI tabbedPaneUi() {
        return new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                shadow = BG;
                darkShadow = BG;
                lightHighlight = TEAL;
                focus = TEAL;
                tabInsets = new Insets(10, 18, 10, 18);
                selectedTabPadInsets = new Insets(1, 1, 1, 1);
                tabAreaInsets = new Insets(6, 0, 8, 0);
                contentBorderInsets = new Insets(0, 0, 0, 0);
            }

            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint paint = isSelected
                        ? new GradientPaint(x, y, new Color(55, 81, 140), x + w, y + h, new Color(34, 54, 105))
                        : new GradientPaint(x, y, new Color(18, 23, 38), x + w, y + h, new Color(25, 31, 49));
                g2.setPaint(paint);
                g2.fillRoundRect(x + 1, y + 1, w - 2, h - 2, 14, 14);
                g2.dispose();
            }

            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isSelected ? GOLD : new Color(83, 97, 132));
                g2.setStroke(new BasicStroke(isSelected ? 1.8f : 1.0f));
                g2.drawRoundRect(x + 1, y + 1, w - 3, h - 3, 14, 14);
                g2.dispose();
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
            }

            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, Rectangle[] rects, int tabIndex, Rectangle iconRect, Rectangle textRect, boolean isSelected) {
            }
        };
    }

    static JPanel card() {
        SurfacePanel panel = new SurfacePanel(new BorderLayout(12, 12), new Color(14, 19, 32, 242), new Color(33, 42, 68, 242));
        panel.setBorder(BorderFactory.createCompoundBorder(new GlowBorder(new Color(88, 103, 143), new Color(255, 194, 85, 185), 16), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        return panel;
    }

    static JPanel dialogPanel(LayoutManager layout) {
        SurfacePanel panel = new SurfacePanel(layout, new Color(8, 12, 24), new Color(24, 31, 52));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return panel;
    }

    static boolean confirmDialog(Component parent, String title, JComponent body) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner == null ? JOptionPane.getRootFrame() : owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        final boolean[] accepted = {false};
        JButton ok = primaryButton("OK");
        JButton cancel = secondaryButton("Cancelar");
        ok.addActionListener(event -> {
            accepted[0] = true;
            dialog.dispose();
        });
        cancel.addActionListener(event -> dialog.dispose());
        JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(cancel);
        actions.add(ok);
        JPanel root = dialogPanel(new BorderLayout(12, 12));
        root.add(body, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(Math.max(360, dialog.getWidth()), Math.max(180, dialog.getHeight())));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return accepted[0];
    }

    static boolean yesNoDialog(Component parent, String title, String message) {
        JPanel body = transparent(new BorderLayout(8, 8));
        body.add(label(message), BorderLayout.CENTER);
        return confirmDialog(parent, title, body);
    }

    static void messageDialog(Component parent, String title, String message) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner == null ? JOptionPane.getRootFrame() : owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        JTextArea area = textArea();
        area.setEditable(false);
        area.setText(message);
        area.setPreferredSize(new Dimension(420, Math.min(180, Math.max(70, message.length() / 3))));
        JButton ok = primaryButton("OK");
        ok.addActionListener(event -> dialog.dispose());
        JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(ok);
        JPanel root = dialogPanel(new BorderLayout(12, 12));
        root.add(area, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    static void viewDialog(Component parent, String title, JComponent body) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner == null ? JOptionPane.getRootFrame() : owner, title, java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        JButton ok = primaryButton("OK");
        ok.addActionListener(event -> dialog.dispose());
        JPanel actions = transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(ok);
        JPanel root = dialogPanel(new BorderLayout(12, 12));
        root.add(body, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);
        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(ok);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(Math.max(520, dialog.getWidth()), Math.max(320, dialog.getHeight())));
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    static JScrollPane scroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(new GlowBorder(new Color(68, 80, 115), TEAL, 12));
        scroll.getViewport().setBackground(INPUT);
        scroll.getViewport().setOpaque(true);
        scroll.setOpaque(false);
        styleScrollBar(scroll.getVerticalScrollBar());
        styleScrollBar(scroll.getHorizontalScrollBar());
        return scroll;
    }

    static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(40);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(52, 58, 77));
        table.setBackground(INPUT);
        table.setForeground(TEXT);
        table.setSelectionBackground(new Color(55, 73, 116));
        table.setSelectionForeground(TEXT);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 13f));
        table.setAutoCreateRowSorter(false);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, selected, focus, row, column);
                component.setForeground(TEXT);
                component.setBackground(selected ? new Color(55, 73, 116) : row % 2 == 0 ? INPUT : new Color(17, 22, 36));
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return component;
            }
        });
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(24, 33, 58));
        header.setForeground(GOLD);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 13f));
        header.setBorder(BorderFactory.createEmptyBorder());
        return table;
    }

    static void styleList(JList<?> list) {
        list.setBackground(INPUT);
        list.setForeground(TEXT);
        list.setSelectionBackground(new Color(55, 73, 116));
        list.setSelectionForeground(TEXT);
        list.setFixedCellHeight(36);
        list.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
    }

    static JPanel titled(String title, JComponent body) {
        JPanel panel = card();
        JPanel heading = transparent(new BorderLayout(0, 6));
        heading.add(title(title, 18f), BorderLayout.NORTH);
        JPanel underline = new JPanel();
        underline.setOpaque(false);
        underline.setPreferredSize(new Dimension(1, 8));
        heading.add(underline, BorderLayout.SOUTH);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    static String text(JTextField field) {
        return field.getText().trim();
    }

    static String password(JPasswordField field) {
        return new String(field.getPassword());
    }

    static int intValue(JSpinner spinner) {
        commit(spinner);
        return ((Number) spinner.getValue()).intValue();
    }

    static double doubleValue(JSpinner spinner) {
        commit(spinner);
        return ((Number) spinner.getValue()).doubleValue();
    }

    static int intValue(JTextField field, int min, int max, String label) {
        try {
            int value = Integer.parseInt(text(field));
            if (value < min || value > max) {
                throw new IllegalArgumentException("El campo " + label + " debe estar entre " + min + " y " + max + ".");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + label + " debe ser un numero entero.");
        }
    }

    static double doubleValue(JTextField field, double min, double max, String label) {
        try {
            double value = Double.parseDouble(text(field).replace(',', '.'));
            if (value < min || value > max) {
                throw new IllegalArgumentException("El campo " + label + " debe estar entre " + min + " y " + max + ".");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("El campo " + label + " debe ser numerico.");
        }
    }

    private static void commit(JSpinner spinner) {
        try {
            spinner.commitEdit();
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Introduce un valor numerico valido.");
        }
    }

    private static void styleText(JTextComponent text) {
        text.setOpaque(true);
        text.setBackground(INPUT);
        text.setForeground(TEXT);
        text.setCaretColor(GOLD);
        text.setSelectionColor(new Color(73, 88, 133));
        text.setSelectedTextColor(TEXT);
        text.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
    }

    private static void styleSpinner(JSpinner spinner) {
        spinner.setOpaque(false);
        JComponent editor = spinner.getEditor();
        editor.setOpaque(false);
        editor.setBorder(BorderFactory.createEmptyBorder());
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            JTextField text = defaultEditor.getTextField();
            text.setEditable(true);
            text.setHorizontalAlignment(JTextField.RIGHT);
            text.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));
            styleText(text);
        }
        for (Component child : spinner.getComponents()) {
            child.setBackground(new Color(25, 31, 49));
            child.setForeground(TEXT);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void styleCombo(JComboBox<?> combo) {
        combo.setOpaque(true);
        combo.setBackground(INPUT);
        combo.setForeground(TEXT);
        combo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        combo.setMaximumRowCount(6);
        combo.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton("v");
                button.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                button.setBackground(new Color(22, 29, 47));
                button.setForeground(GOLD);
                button.setFocusPainted(false);
                button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                return button;
            }
        });
        combo.setBorder(BorderFactory.createCompoundBorder(new GlowBorder(LINE, TEAL, 9), BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        combo.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(comboText(value));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            label.setBackground(selected ? new Color(55, 73, 116) : INPUT);
            label.setForeground(index < 0 ? TEXT : TEXT);
            label.setVerticalAlignment(SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(index < 0 ? Font.BOLD : Font.PLAIN, 13f));
            return label;
        });
    }

    private static String comboText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof TipoPersonaje type) {
            return typeText(type);
        }
        if (value instanceof Enum<?> enumValue) {
            String text = enumValue.name().replace('_', ' ').toLowerCase();
            return text.isBlank() ? "" : Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return value.toString();
    }

    private static void styleScrollBar(JScrollBar bar) {
        if (bar == null) {
            return;
        }
        bar.setOpaque(false);
        bar.setUnitIncrement(16);
        bar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                thumbColor = new Color(74, 91, 138);
                trackColor = new Color(10, 13, 24);
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return zeroButton();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(8, 12, 22));
                g2.fillRoundRect(trackBounds.x + 2, trackBounds.y + 2, Math.max(4, trackBounds.width - 4), Math.max(4, trackBounds.height - 4), 10, 10);
                g2.dispose();
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.width <= 0 || thumbBounds.height <= 0) {
                    return;
                }
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(thumbBounds.x, thumbBounds.y, BLUE, thumbBounds.x + thumbBounds.width, thumbBounds.y + thumbBounds.height, VIOLET));
                g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2, Math.max(4, thumbBounds.width - 4), Math.max(20, thumbBounds.height - 4), 10, 10);
                g2.dispose();
            }

            private JButton zeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }
        });
    }

    static final class Form extends JPanel {
        private int row;

        Form() {
            super(new GridBagLayout());
            setOpaque(false);
        }

        void addRow(String label, Component component) {
            GridBagConstraints left = new GridBagConstraints();
            left.gridx = 0;
            left.gridy = row;
            left.anchor = GridBagConstraints.WEST;
            left.insets = new Insets(8, 0, 8, 14);
            add(small(label), left);

            GridBagConstraints right = new GridBagConstraints();
            right.gridx = 1;
            right.gridy = row;
            right.weightx = 1;
            right.fill = GridBagConstraints.HORIZONTAL;
            right.insets = new Insets(8, 0, 8, 0);
            add(component, right);
            row++;
        }
    }

    static final class BackgroundPanel extends JPanel {
        BackgroundPanel() {
            super(new BorderLayout());
            setOpaque(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Paint paint = new LinearGradientPaint(0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.25f, 0.62f, 1f},
                    new Color[]{new Color(5, 8, 17), new Color(14, 20, 38), new Color(16, 40, 48), new Color(44, 21, 40)});
            g.setPaint(paint);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 16));
            g.setStroke(new BasicStroke(1f));
            int gap = 42;
            for (int x = -getHeight(); x < getWidth(); x += gap) {
                g.drawLine(x, getHeight(), x + getHeight(), 0);
            }
            g.setColor(new Color(255, 194, 85, 36));
            g.fillOval(-130, -110, 320, 320);
            g.setColor(new Color(58, 223, 201, 34));
            g.fillOval(getWidth() - 260, getHeight() - 240, 420, 360);
            g.setColor(new Color(255, 255, 255, 20));
            for (int i = 0; i < 42; i++) {
                int x = 18 + (i * 97) % Math.max(98, getWidth() - 36);
                int y = 14 + (i * 61) % Math.max(70, getHeight() - 28);
                g.fillOval(x, y, 2, 2);
            }
            g.dispose();
        }
    }

    static final class ArenaPanel extends JPanel {
        private String scene = "combate";
        private String[] rankingNames = {"-", "-", "-"};

        ArenaPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(480, 300));
        }

        void setScene(String scene) {
            this.scene = scene == null ? "combate" : scene;
            repaint();
        }

        void setRankingNames(String first, String second, String third) {
            rankingNames = new String[]{
                    first == null || first.isBlank() ? "-" : first,
                    second == null || second.isBlank() ? "-" : second,
                    third == null || third.isBlank() ? "-" : third
            };
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setColor(new Color(3, 6, 12, 190));
            g.fillRoundRect(10, 10, w - 20, h - 20, 22, 22);
            g.setPaint(new GradientPaint(18, 18, new Color(255, 194, 85, 125), w - 18, h - 18, new Color(58, 223, 201, 135)));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(18, 18, w - 36, h - 36, 18, 18);

            drawScene(g, w, h);

            g.setColor(TEXT);
            g.setFont(getFont().deriveFont(Font.BOLD, 25f));
            g.drawString("ARENA METPROG", 42, 60);
            g.setColor(MUTED);
            g.setFont(getFont().deriveFont(Font.PLAIN, 14f));
            g.drawString(sceneCopy(), 42, 84);
            g.dispose();
        }

        private void drawScene(Graphics2D g, int w, int h) {
            switch (scene) {
                case "jugadores" -> drawPlayersScene(g, w, h);
                case "operadores" -> drawOperatorsScene(g, w, h);
                case "razas" -> drawRacesScene(g, w, h);
                case "ranking" -> drawRankingScene(g, w, h);
                case "persistencia" -> drawPersistenceScene(g, w, h);
                default -> drawCombatScene(g, w, h);
            }
        }

        private String sceneCopy() {
            return switch (scene) {
                case "jugadores" -> "Alta, acceso y gestion visual para jugadores";
                case "operadores" -> "Panel de control para validacion, bloqueo y supervision";
                case "razas" -> "Vampiros, licantropos y cazadores con identidad propia";
                case "ranking" -> "Podio, oro y progreso competitivo de toda la arena";
                case "persistencia" -> "Datos, historial y estado del sistema persistidos";
                default -> "Combates, desafios, ranking y fichas visuales persistentes";
            };
        }

        private void drawCombatScene(Graphics2D g, int w, int h) {
            g.setColor(new Color(58, 223, 201, 52));
            g.fillOval(w / 2 - 148, h / 2 - 70, 296, 140);
            g.setColor(new Color(255, 194, 85, 34));
            g.fillOval(w / 2 - 184, h / 2 - 92, 368, 184);
            drawPosterFighter(g, w / 2 - 180, h / 2 - 36, VAMPIRE, true, "vampire");
            drawPosterFighter(g, w / 2 + 82, h / 2 - 44, GOLD, false, "hunter");
        }

        private void drawPlayersScene(Graphics2D g, int w, int h) {
            for (int i = 0; i < 3; i++) {
                int x = 88 + i * 156;
                g.setColor(new Color(255, 255, 255, 16));
                g.fillRoundRect(x, 120, 122, 124, 18, 18);
                g.setColor(new Color(95 + i * 25, 154, 255 - i * 30, 80));
                g.drawRoundRect(x, 120, 122, 124, 18, 18);
                drawAvatarBust(g, x + 24, 138, i == 0 ? VAMPIRE : i == 1 ? HUNTER : WOLF);
            }
        }

        private void drawOperatorsScene(Graphics2D g, int w, int h) {
            g.setColor(new Color(121, 92, 255, 42));
            g.fillRoundRect(98, 126, w - 196, 108, 18, 18);
            g.setColor(new Color(255, 255, 255, 25));
            g.drawRoundRect(98, 126, w - 196, 108, 18, 18);
            for (int i = 0; i < 4; i++) {
                int x = 126 + i * 148;
                g.setColor(new Color(73, 221, 216, 86));
                g.fillRoundRect(x, 156, 116, 22, 10, 10);
                g.setColor(new Color(255, 194, 85, 80));
                g.fillRoundRect(x, 192, 88, 12, 10, 10);
            }
            drawAvatarBust(g, w / 2 - 32, 94, VIOLET);
        }

        private void drawRacesScene(Graphics2D g, int w, int h) {
            drawPosterFighter(g, 78, 112, VAMPIRE, true, "vampire");
            drawPosterFighter(g, w / 2 - 46, 102, WOLF, true, "wolf");
            drawPosterFighter(g, w - 244, 112, HUNTER, false, "hunter");
        }

        private void drawRankingScene(Graphics2D g, int w, int h) {
            int baseY = h - 86;
            drawPodiumBlock(g, w / 2 - 164, baseY - 42, 78, 42, BLUE, "2");
            drawPodiumBlock(g, w / 2 - 52, baseY - 74, 92, 74, GOLD, "1");
            drawPodiumBlock(g, w / 2 + 72, baseY - 28, 72, 28, CRIMSON, "3");
            drawRankingName(g, w / 2 - 124, baseY - 56, rankingNames[1], BLUE);
            drawRankingName(g, w / 2 - 6, baseY - 92, rankingNames[0], GOLD);
            drawRankingName(g, w / 2 + 108, baseY - 42, rankingNames[2], CRIMSON);
        }

        private void drawPersistenceScene(Graphics2D g, int w, int h) {
            g.setColor(new Color(255, 194, 85, 32));
            g.fillRoundRect(122, 124, 146, 98, 18, 18);
            g.setColor(new Color(255, 255, 255, 22));
            g.drawRoundRect(122, 124, 146, 98, 18, 18);
            g.setColor(new Color(73, 221, 216, 62));
            g.fillOval(w / 2 - 44, 126, 88, 88);
            g.setColor(new Color(166, 105, 255, 80));
            for (int i = 0; i < 5; i++) {
                g.drawLine(w / 2 + 36, 168, w - 148 + i * 2, 122 + i * 24);
            }
            g.setColor(new Color(255, 255, 255, 38));
            g.fillRoundRect(w - 198, 124, 92, 112, 18, 18);
        }

        private void drawPosterFighter(Graphics2D g, int x, int y, Color accent, boolean left, String mode) {
            g.setColor(new Color(255, 255, 255, 30));
            g.fillOval(x - 10, y + 134, 126, 20);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 30));
            g.fillOval(x - 18, y - 8, 136, 154);

            g.setPaint(new GradientPaint(x + 14, y + 44, accent.darker(), x + 88, y + 134, accent.brighter()));
            int[] capeX = {x + 10, x + 78, x + 104, x - 12};
            int[] capeY = {y + 40, y + 36, y + 132, y + 138};
            g.fillPolygon(capeX, capeY, capeX.length);

            g.setColor(new Color(11, 14, 24));
            g.fillRoundRect(x + 20, y + 46, 56, 78, 22, 22);
            g.setColor(new Color(244, 229, 212));
            g.fillOval(x + 28, y + 6, 38, 42);
            g.setColor(new Color(5, 8, 16));
            g.fillOval(x + 41, y + 22, 4, 4);
            g.fillOval(x + 50, y + 22, 4, 4);
            g.setColor(accent.darker());
            g.fillArc(x + 24, y - 2, 46, 22, 0, 180);

            if ("vampire".equals(mode)) {
                g.setStroke(new BasicStroke(3f));
                g.setColor(accent);
                g.drawLine(x + 6, y + 30, x + 20, y + 72);
                g.drawLine(x + 78, y + 34, x + 98, y + 78);
                g.setColor(GOLD);
                g.drawLine(x + 68, y + 8, x + 94, y - 6);
            } else if ("wolf".equals(mode)) {
                int[] x1 = {x + 24, x + 34, x + 40};
                int[] y1 = {y + 8, y - 10, y + 8};
                int[] x2 = {x + 52, x + 60, x + 66};
                int[] y2 = {y + 8, y - 8, y + 10};
                g.fillPolygon(x1, y1, 3);
                g.fillPolygon(x2, y2, 3);
                g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(x - 10, y + 64, x + 14, y + 54);
                g.drawLine(x + 80, y + 52, x + 104, y + 38);
            } else {
                g.setStroke(new BasicStroke(4f));
                g.setColor(accent);
                if (left) {
                    g.drawLine(x + 76, y + 44, x + 104, y + 16);
                    g.drawLine(x + 104, y + 16, x + 114, y + 28);
                } else {
                    g.drawLine(x + 18, y + 44, x - 10, y + 16);
                    g.drawLine(x - 10, y + 16, x - 20, y + 28);
                }
            }
        }

        private void drawAvatarBust(Graphics2D g, int x, int y, Color accent) {
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 54));
            g.fillOval(x - 12, y - 4, 82, 96);
            g.setColor(new Color(246, 231, 214));
            g.fillOval(x + 10, y, 34, 36);
            g.setPaint(new GradientPaint(x, y + 30, accent.darker(), x + 58, y + 88, accent.brighter()));
            g.fillRoundRect(x, y + 30, 56, 58, 18, 18);
            g.setColor(new Color(9, 11, 18));
            g.fillArc(x + 8, y - 2, 38, 18, 0, 180);
        }

        private void drawPodiumBlock(Graphics2D g, int x, int y, int width, int height, Color color, String place) {
            g.setPaint(new GradientPaint(x, y, color, x, y + height, color.darker()));
            g.fillRoundRect(x, y, width, height, 16, 16);
            g.setColor(new Color(255, 255, 255, 90));
            g.drawRoundRect(x, y, width, height, 16, 16);
            g.setColor(TEXT);
            g.setFont(getFont().deriveFont(Font.BOLD, 22f));
            g.drawString(place, x + width / 2 - 6, y + height / 2 + 8);
        }

        private void drawRankingName(Graphics2D g, int x, int y, String name, Color color) {
            String trimmed = name.length() > 12 ? name.substring(0, 12) + "…" : name;
            g.setColor(new Color(3, 6, 12, 210));
            int width = Math.max(74, g.getFontMetrics(getFont().deriveFont(Font.BOLD, 12f)).stringWidth(trimmed) + 18);
            g.fillRoundRect(x - width / 2, y - 16, width, 24, 12, 12);
            g.setColor(color);
            g.drawRoundRect(x - width / 2, y - 16, width, 24, 12, 12);
            g.setColor(TEXT);
            g.setFont(getFont().deriveFont(Font.BOLD, 12f));
            int textWidth = g.getFontMetrics().stringWidth(trimmed);
            g.drawString(trimmed, x - textWidth / 2, y);
        }
    }

    static final class SurfacePanel extends JPanel {
        private final Color start;
        private final Color end;

        SurfacePanel(LayoutManager layout, Color start, Color end) {
            super(layout);
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g.setColor(new Color(255, 255, 255, 10));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 18, 18);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    static final class GlowBorder extends AbstractBorder {
        private final Color color;
        private final Color focusColor;
        private final int radius;

        GlowBorder(Color color, Color focusColor, int radius) {
            this.color = color;
            this.focusColor = focusColor;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(component.hasFocus() || focusInside(component) ? focusColor : color);
            g.setStroke(new BasicStroke(component.hasFocus() || focusInside(component) ? 1.8f : 1.1f));
            g.draw(new RoundRectangle2D.Double(x + 0.8, y + 0.8, width - 2.0, height - 2.0, radius, radius));
            g.dispose();
        }

        @Override
        public Insets getBorderInsets(Component component) {
            return new Insets(8, 8, 8, 8);
        }

        @Override
        public Insets getBorderInsets(Component component, Insets insets) {
            insets.set(8, 8, 8, 8);
            return insets;
        }

        private boolean focusInside(Component component) {
            if (component instanceof java.awt.Container container) {
                for (Component child : container.getComponents()) {
                    if (child.hasFocus() || focusInside(child)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private static final class GlowButton extends JButton {
        private final Color start;
        private final Color end;
        private final Color fg;

        GlowButton(String text, Color start, Color end, Color fg) {
            super(text);
            this.start = start;
            this.end = end;
            this.fg = fg;
            setUI(new BasicButtonUI());
            setOpaque(false);
            setContentAreaFilled(false);
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            boolean selected = Boolean.TRUE.equals(getClientProperty("speedSelected"));
            Color a = selected ? Ui.TEAL : getModel().isPressed() ? end.darker() : getModel().isRollover() ? start.brighter() : start;
            Color b = selected ? Ui.GOLD : getModel().isPressed() ? start.darker() : getModel().isRollover() ? end.brighter() : end;
            g.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g.setColor(new Color(255, 255, 255, selected ? 34 : 18));
            g.fillRoundRect(2, 2, getWidth() - 4, Math.max(6, getHeight() / 2), 12, 12);
            g.setColor(new Color(255, 255, 255, selected ? 150 : getModel().isRollover() ? 95 : 45));
            g.setStroke(new BasicStroke(selected ? 2.4f : 1.2f));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g.dispose();
            setForeground(fg);
            super.paintComponent(graphics);
        }
    }

}
