package es.urjc.metprog.gui;

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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxUI;
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

    static JPanel card() {
        SurfacePanel panel = new SurfacePanel(new BorderLayout(12, 12), new Color(18, 23, 38, 238), new Color(33, 41, 65, 238));
        panel.setBorder(BorderFactory.createCompoundBorder(new GlowBorder(new Color(82, 96, 134), new Color(255, 194, 85, 185), 13), BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    static JPanel dialogPanel(LayoutManager layout) {
        SurfacePanel panel = new SurfacePanel(layout, new Color(9, 13, 25), new Color(24, 31, 52));
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
        scroll.setBorder(new GlowBorder(new Color(68, 80, 115), TEAL, 10));
        scroll.getViewport().setBackground(INPUT);
        scroll.getViewport().setOpaque(true);
        scroll.setOpaque(false);
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
        table.setRowHeight(36);
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
        header.setBackground(new Color(32, 40, 64));
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
        list.setFixedCellHeight(32);
    }

    static JPanel titled(String title, JComponent body) {
        JPanel panel = card();
        panel.add(title(title, 18f), BorderLayout.NORTH);
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
        combo.setBorder(BorderFactory.createCompoundBorder(new GlowBorder(LINE, TEAL, 9), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        combo.setRenderer((list, value, index, selected, focus) -> {
            JLabel label = new JLabel(comboText(value));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            label.setBackground(selected ? new Color(55, 73, 116) : INPUT);
            label.setForeground(index < 0 ? GOLD : TEXT);
            label.setFont(label.getFont().deriveFont(index < 0 ? Font.BOLD : Font.PLAIN, 14f));
            return label;
        });
    }

    private static String comboText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Enum<?> enumValue) {
            String text = enumValue.name().replace('_', ' ').toLowerCase();
            return text.isBlank() ? "" : Character.toUpperCase(text.charAt(0)) + text.substring(1);
        }
        return value.toString();
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
                    new float[]{0f, 0.36f, 0.68f, 1f},
                    new Color[]{new Color(6, 8, 17), new Color(29, 13, 42), new Color(12, 42, 48), new Color(47, 23, 36)});
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
            g.dispose();
        }
    }

    static final class ArenaPanel extends JPanel {
        ArenaPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(480, 300));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            g.setColor(new Color(3, 6, 12, 180));
            g.fillRoundRect(10, 10, w - 20, h - 20, 18, 18);
            g.setPaint(new GradientPaint(18, 18, new Color(255, 194, 85, 135), w - 18, h - 18, new Color(58, 223, 201, 135)));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(18, 18, w - 36, h - 36, 16, 16);
            g.setColor(new Color(58, 223, 201, 70));
            g.fillOval(w / 2 - 116, h / 2 - 58, 232, 116);
            g.setColor(new Color(232, 72, 101, 170));
            g.fillRoundRect(w / 2 - 165, h / 2 - 18, 92, 88, 12, 12);
            g.setColor(new Color(255, 194, 85, 185));
            g.fillRoundRect(w / 2 + 76, h / 2 - 40, 92, 112, 12, 12);
            g.setColor(TEXT);
            g.setFont(getFont().deriveFont(Font.BOLD, 25f));
            g.drawString("ARENA METPROG", 42, 60);
            g.setColor(MUTED);
            g.setFont(getFont().deriveFont(Font.PLAIN, 14f));
            g.drawString("Combates, desafios y ranking persistente", 42, 84);
            g.dispose();
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
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
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
            g.setColor(new Color(255, 255, 255, selected ? 150 : getModel().isRollover() ? 95 : 45));
            g.setStroke(new BasicStroke(selected ? 2.4f : 1.2f));
            g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 12, 12);
            g.dispose();
            setForeground(fg);
            super.paintComponent(graphics);
        }
    }

}
