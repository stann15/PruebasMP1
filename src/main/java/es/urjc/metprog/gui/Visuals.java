package es.urjc.metprog.gui;

import es.urjc.metprog.domain.character.Personaje;
import es.urjc.metprog.domain.character.TipoPersonaje;
import es.urjc.metprog.domain.ranking.EntradaRanking;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.List;

final class Visuals {
    private Visuals() {
    }

    static JPanel heroBanner(String eyebrow, String title, String copy, Color start, Color end) {
        JPanel panel = new BannerPanel(start, end);
        panel.setLayout(new BorderLayout(0, 12));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new Color(112, 126, 168), new Color(255, 194, 85, 180), 18),
                BorderFactory.createEmptyBorder(22, 24, 22, 24)));
        panel.add(Ui.small(eyebrow), BorderLayout.NORTH);

        JPanel text = Ui.transparent(new BorderLayout(0, 8));
        JLabel heading = Ui.title(title, 28f);
        heading.setForeground(Ui.TEXT);
        JLabel body = Ui.label(copy);
        body.setForeground(Ui.MUTED);
        text.add(heading, BorderLayout.NORTH);
        text.add(body, BorderLayout.CENTER);
        panel.add(text, BorderLayout.CENTER);
        return panel;
    }

    static JPanel heroBanner(String eyebrow, String title, String copy) {
        return heroBanner(eyebrow, title, copy, new Color(16, 26, 42), new Color(19, 48, 68));
    }

    static JPanel badge(String text, Color color) {
        JPanel badge = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 6, 0));
        badge.setBorder(BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 120), color, 12),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        JLabel label = Ui.small(text);
        label.setForeground(color);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 12f));
        badge.add(label);
        return badge;
    }

    static JPanel chips(String... entries) {
        JPanel panel = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        Color[] colors = {Ui.GOLD, Ui.TEAL, Ui.BLUE, Ui.CRIMSON, Ui.VIOLET, Ui.GREEN};
        for (int i = 0; i < entries.length; i++) {
            panel.add(badge(entries[i], colors[i % colors.length]));
        }
        return panel;
    }

    static JButton chipButton(String text, Color color, ActionListener action) {
        JButton button = Ui.ghostButton(text);
        button.setForeground(color);
        button.setBorder(BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 110), color, 12),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }

    static JPanel rankingPodium(List<EntradaRanking> ranking) {
        JPanel row = Ui.transparent(new GridLayout(1, 3, 14, 14));
        if (ranking.isEmpty()) {
            row.add(emptyPodium("I"));
            row.add(emptyPodium("II"));
            row.add(emptyPodium("III"));
            return row;
        }
        row.add(podiumCard(ranking, 1, 2, Ui.BLUE, "II"));
        row.add(podiumCard(ranking, 0, 1, Ui.GOLD, "I"));
        row.add(podiumCard(ranking, 2, 3, Ui.CRIMSON, "III"));
        return row;
    }

    static PortraitTabs portraitTabs() {
        return new PortraitTabs();
    }

    static PortraitPreviewPanel portraitPreview() {
        return new PortraitPreviewPanel();
    }

    static JPanel characterSummary(Personaje character) {
        JPanel panel = Ui.card();
        panel.add(Ui.title(character.getNombre(), 22f), BorderLayout.NORTH);
        JPanel body = Ui.transparent(new BorderLayout(12, 12));
        body.add(new PortraitPanel(character.getTipo(), character.getNombre(), portraitSubtitle(character.getTipo())), BorderLayout.CENTER);
        body.add(chips(
                Ui.typeText(character.getTipo()),
                "Poder " + character.getPoder(),
                "Oro " + character.getOro(),
                "Salud " + character.getSaludActual()), BorderLayout.SOUTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel emptyPodium(String place) {
        JPanel card = Ui.card();
        JLabel rank = Ui.title(place, 20f);
        rank.setForeground(Ui.MUTED);
        JLabel copy = Ui.small("Esperando contendiente");
        copy.setHorizontalAlignment(SwingConstants.CENTER);
        rank.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(rank, BorderLayout.NORTH);
        card.add(copy, BorderLayout.CENTER);
        return card;
    }

    private static JPanel podiumCard(List<EntradaRanking> ranking, int index, int place, Color color, String roman) {
        if (ranking.size() <= index) {
            return emptyPodium(roman);
        }
        EntradaRanking entry = ranking.get(index);
        JPanel card = new BannerPanel(new Color(18, 25, 42), new Color(28, 36, 58));
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
                new Ui.GlowBorder(new Color(color.getRed(), color.getGreen(), color.getBlue(), 115), color, 18),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));

        JLabel pos = Ui.title("#" + place, 22f);
        pos.setForeground(color);
        JLabel player = Ui.title(entry.nickJugador(), 20f);
        JLabel character = Ui.small(entry.nombrePersonaje());
        character.setForeground(Ui.MUTED);
        JPanel header = Ui.transparent(new BorderLayout(0, 4));
        header.add(pos, BorderLayout.NORTH);
        header.add(player, BorderLayout.CENTER);
        header.add(character, BorderLayout.SOUTH);

        JPanel stats = Ui.transparent(new GridLayout(1, 2, 8, 0));
        stats.add(statBox("Oro", String.valueOf(entry.oroActual()), Ui.GOLD));
        stats.add(statBox("Victorias", String.valueOf(entry.victorias()), Ui.TEAL));
        JPanel footer = Ui.transparent(new BorderLayout(0, 8));
        footer.add(stats, BorderLayout.CENTER);
        footer.add(Ui.small("Derrotas: " + entry.derrotas()), BorderLayout.SOUTH);

        card.add(header, BorderLayout.NORTH);
        card.add(new PortraitPanel(entry.tipoPersonaje(), entry.nombrePersonaje(), "Jugador " + entry.nickJugador()), BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);
        return card;
    }

    private static JPanel statBox(String label, String value, Color color) {
        JPanel box = Ui.transparent(new BorderLayout(0, 4));
        JLabel l = Ui.small(label);
        JLabel v = Ui.title(value, 18f);
        v.setForeground(color);
        box.add(l, BorderLayout.NORTH);
        box.add(v, BorderLayout.CENTER);
        return box;
    }

    static String portraitSubtitle(TipoPersonaje type) {
        return switch (type) {
            case VAMPIRO -> "Sangre y disciplina";
            case LICANTROPO -> "Rabia y transformacion";
            case CAZADOR -> "Pulso y talento";
        };
    }

    static final class PortraitTabs extends JTabbedPane {
        private String currentTitle = "";
        private TipoPersonaje currentType = TipoPersonaje.VAMPIRO;
        private String currentSubtitle = portraitSubtitle(TipoPersonaje.VAMPIRO);

        PortraitTabs() {
            super(JTabbedPane.TOP);
            setOpaque(false);
            setFocusable(false);
            setUI(Ui.tabbedPaneUi());
            setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
            setBorder(BorderFactory.createEmptyBorder());
        }

        void setPortrait(TipoPersonaje type, String name, String subtitle) {
            String title = name == null || name.isBlank() ? Ui.typeText(type) : name;
            currentType = type;
            currentSubtitle = subtitle;
            JPanel content = new PortraitPanel(type, title, subtitle);
            if (getTabCount() == 0) {
                addTab(title, content);
                setTabComponentAt(0, closableTitle(title));
                setSelectedIndex(0);
            } else {
                setComponentAt(0, content);
                setTitleAt(0, title);
                setTabComponentAt(0, closableTitle(title));
            }
            currentTitle = title;
            revalidate();
            repaint();
        }

        String currentTitle() {
            return currentTitle;
        }

        private void restorePortrait() {
            if (getTabCount() == 0) {
                return;
            }
            setComponentAt(0, new PortraitPanel(currentType, currentTitle, currentSubtitle));
            setTitleAt(0, currentTitle);
            setTabComponentAt(0, closableTitle(currentTitle));
            revalidate();
            repaint();
        }

        private JComponent closableTitle(String title) {
            JPanel tab = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JLabel label = Ui.small(title);
            label.setForeground(Ui.TEXT);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13f));
            JButton close = new JButton("x");
            close.setFocusable(false);
            close.setOpaque(false);
            close.setContentAreaFilled(false);
            close.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            close.setForeground(Ui.MUTED);
            close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            close.addActionListener(event -> {
                if (getTabCount() == 0) {
                    return;
                }
                setComponentAt(0, minimizedPanel());
                setTitleAt(0, currentTitle);
                setTabComponentAt(0, minimizedTitle());
                revalidate();
                repaint();
            });
            tab.add(label);
            tab.add(close);
            return tab;
        }

        private JComponent minimizedTitle() {
            JPanel tab = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
            JButton button = Ui.ghostButton("Vista previa");
            button.setForeground(Ui.MUTED);
            button.setFont(button.getFont().deriveFont(Font.BOLD, 13f));
            button.setToolTipText("Mostrar retrato otra vez");
            button.addActionListener(event -> restorePortrait());
            tab.add(button);
            return tab;
        }

        private JPanel minimizedPanel() {
            JPanel panel = Ui.transparent(new BorderLayout(0, 8));
            panel.setBorder(BorderFactory.createCompoundBorder(
                    new Ui.GlowBorder(new Color(80, 94, 132), Ui.TEAL, 16),
                    BorderFactory.createEmptyBorder(20, 18, 20, 18)));
            JLabel title = Ui.title("Vista previa minimizada", 16f);
            JLabel copy = Ui.small("Cambia la raza o escribe el nombre para regenerar el retrato.");
            panel.add(title, BorderLayout.NORTH);
            panel.add(copy, BorderLayout.CENTER);
            return panel;
        }
    }

    static final class PortraitPreviewPanel extends JPanel {
        private TipoPersonaje type = TipoPersonaje.VAMPIRO;
        private String title = Ui.typeText(TipoPersonaje.VAMPIRO);
        private String subtitle = "Edad, sangre y disciplina";
        private boolean collapsed;

        PortraitPreviewPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(320, 162));
            setMinimumSize(new Dimension(280, 150));
            render();
        }

        void setPortrait(TipoPersonaje type, String name, String subtitle) {
            this.type = type;
            this.title = name == null || name.isBlank() ? Ui.typeText(type) : name;
            this.subtitle = subtitle == null ? "" : subtitle;
            this.collapsed = false;
            render();
        }

        private void render() {
            removeAll();
            setLayout(new BorderLayout(0, 8));

            JPanel shell = new BannerPanel(new Color(14, 19, 34), new Color(24, 32, 56));
            shell.setLayout(new BorderLayout(0, 8));
            shell.setBorder(BorderFactory.createCompoundBorder(
                    new Ui.GlowBorder(new Color(88, 103, 143), Ui.GOLD, 16),
                    BorderFactory.createEmptyBorder(10, 12, 12, 12)));

            JPanel head = Ui.transparent(new BorderLayout(8, 0));
            JLabel tag = Ui.small(collapsed ? "Vista previa cerrada" : title);
            tag.setForeground(collapsed ? Ui.MUTED : Ui.TEXT);
            tag.setFont(tag.getFont().deriveFont(Font.BOLD, 13f));
            JButton close = new JButton("x");
            close.setFocusable(false);
            close.setOpaque(false);
            close.setContentAreaFilled(false);
            close.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
            close.setForeground(Ui.MUTED);
            close.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            close.addActionListener(event -> {
                collapsed = true;
                render();
            });
            head.add(tag, BorderLayout.WEST);
            if (!collapsed) {
                head.add(close, BorderLayout.EAST);
            }
            shell.add(head, BorderLayout.NORTH);

            if (collapsed) {
                JPanel body = Ui.transparent(new BorderLayout());
                body.add(Ui.small("Cambia la raza o escribe el nombre para regenerar la vista."), BorderLayout.CENTER);
                shell.add(body, BorderLayout.CENTER);
            } else {
                shell.add(new PortraitPanel(type, title, subtitle), BorderLayout.CENTER);
            }

            add(shell, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    private static final class BannerPanel extends JPanel {
        private final Color start;
        private final Color end;

        private BannerPanel(Color start, Color end) {
            this.start = start;
            this.end = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Paint paint = new LinearGradientPaint(0, 0, getWidth(), getHeight(),
                    new float[]{0f, 0.48f, 1f},
                    new Color[]{start, new Color(17, 23, 38), end});
            g.setPaint(paint);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            g.setColor(new Color(255, 255, 255, 14));
            for (int i = 0; i < 10; i++) {
                g.drawLine(26 + i * 34, 0, 0, 26 + i * 34);
                g.drawLine(getWidth() - 26 - i * 34, getHeight(), getWidth(), getHeight() - 26 - i * 34);
            }
            g.setColor(new Color(255, 194, 85, 32));
            g.fillOval(-70, -60, 190, 190);
            g.setColor(new Color(58, 223, 201, 26));
            g.fillOval(getWidth() - 150, getHeight() - 150, 220, 220);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class PortraitPanel extends JPanel {
        private final TipoPersonaje type;
        private final String title;
        private final String subtitle;

        private PortraitPanel(TipoPersonaje type, String title, String subtitle) {
            this.type = type;
            this.title = title;
            this.subtitle = subtitle;
            setOpaque(false);
            setPreferredSize(new Dimension(320, 190));
            setMinimumSize(new Dimension(260, 170));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            boolean compact = h < 170 || w < 290;
            Color accent = Ui.accentForType(type);
            Color accentSoft = Ui.softAccentForType(type);

            g.setPaint(new LinearGradientPaint(0, 0, w, h,
                    new float[]{0f, 0.52f, 1f},
                    new Color[]{new Color(11, 15, 27), accentSoft, new Color(22, 30, 48)}));
            g.fillRoundRect(0, 0, w, h, 18, 18);
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 120));
            g.setStroke(new BasicStroke(1.6f));
            g.drawRoundRect(1, 1, w - 3, h - 3, 18, 18);

            double artScale = Math.min((w - 10d) / 320d, (h - 10d) / 190d);
            artScale = Math.min(1d, Math.max(0.58d, artScale));
            double artWidth = 320d * artScale;
            double artHeight = 190d * artScale;
            int artX = compact ? Math.max(4, (int) Math.round(w - artWidth - 4)) : 0;
            int artY = compact ? Math.max(6, (int) Math.round(h - artHeight - 2)) : 0;
            Graphics2D art = (Graphics2D) g.create();
            art.translate(artX, artY);
            art.scale(artScale, artScale);
            drawBackdrop(art, 320, 190, accent, accentSoft);
            drawPortrait(art, 320, 190, accent);
            art.dispose();

            JLabelPainter.draw(g, title, 18, compact ? 28 : 32, Ui.TEXT, compact ? 15f : 20f, Font.BOLD);
            if (!compact) {
                JLabelPainter.draw(g, subtitle == null ? "" : subtitle, 18, 54, Ui.MUTED, 12f, Font.PLAIN);
                drawTag(g, 18, h - 38, Ui.typeText(type), accent);
                drawTag(g, 126, h - 38, roleCopy(type), new Color(255, 255, 255, 80));
            }
            g.dispose();
        }

        private void drawTag(Graphics2D g, int x, int y, String text, Color color) {
            g.setFont(g.getFont().deriveFont(Font.BOLD, 11f));
            int width = g.getFontMetrics().stringWidth(text) + 18;
            g.setColor(new Color(8, 12, 22, 210));
            g.fillRoundRect(x, y, width, 22, 11, 11);
            g.setColor(color);
            g.drawRoundRect(x, y, width, 22, 11, 11);
            g.setColor(Ui.TEXT);
            g.drawString(text, x + 9, y + 15);
        }

        private void drawBackdrop(Graphics2D g, int w, int h, Color accent, Color soft) {
            g.setColor(new Color(255, 255, 255, 16));
            for (int i = 0; i < 8; i++) {
                g.drawLine(w - 30 - i * 24, 0, w, 30 + i * 24);
            }
            g.setColor(new Color(soft.getRed(), soft.getGreen(), soft.getBlue(), 55));
            g.fill(new Ellipse2D.Double(w - 176, 12, 128, 128));
            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 44));
            g.fill(new Ellipse2D.Double(30, h - 110, 150, 110));
            if (type == TipoPersonaje.VAMPIRO) {
                g.setColor(new Color(255, 194, 85, 28));
                g.fillRect(w - 76, 54, 10, 72);
                g.fillRect(w - 56, 38, 10, 88);
                g.fillRect(w - 36, 62, 10, 64);
            } else if (type == TipoPersonaje.LICANTROPO) {
                g.setColor(new Color(255, 255, 255, 16));
                g.drawLine(36, h - 28, 72, h - 92);
                g.drawLine(64, h - 28, 92, h - 84);
                g.drawLine(98, h - 28, 124, h - 76);
            } else {
                g.setColor(new Color(255, 194, 85, 26));
                g.fillRoundRect(w - 76, h - 92, 40, 52, 10, 10);
                g.setColor(new Color(73, 221, 216, 34));
                g.fillOval(w - 88, h - 108, 62, 62);
            }
        }

        private void drawPortrait(Graphics2D g, int w, int h, Color accent) {
            int baseX = w - 152;
            int baseY = 26;
            g.setColor(new Color(255, 255, 255, 24));
            g.fillOval(baseX - 34, baseY + 118, 146, 24);

            g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 38));
            g.fillOval(baseX - 18, baseY + 4, 124, 148);

            g.setPaint(new GradientPaint(baseX + 2, baseY + 62, accent.darker(), baseX + 84, baseY + 138, accent.brighter()));
            int[] cloakX = {baseX + 8, baseX + 84, baseX + 102, baseX - 12};
            int[] cloakY = {baseY + 60, baseY + 56, baseY + 140, baseY + 144};
            g.fillPolygon(cloakX, cloakY, cloakX.length);

            g.setColor(new Color(10, 14, 24));
            g.fillRoundRect(baseX + 18, baseY + 64, 54, 68, 22, 22);
            g.setColor(new Color(242, 226, 206));
            g.fillOval(baseX + 24, baseY + 16, 44, 48);
            g.setColor(new Color(7, 10, 18));
            g.fillOval(baseX + 38, baseY + 34, 4, 4);
            g.fillOval(baseX + 50, baseY + 34, 4, 4);
            g.drawArc(baseX + 36, baseY + 44, 20, 8, 190, 160);
            g.setColor(accent.darker());
            switch (type) {
                case VAMPIRO -> {
                    g.fillArc(baseX + 18, baseY + 6, 58, 24, 0, 180);
                    g.setStroke(new BasicStroke(3f));
                    g.drawLine(baseX + 6, baseY + 46, baseX + 18, baseY + 88);
                    g.drawLine(baseX + 74, baseY + 46, baseX + 94, baseY + 94);
                    g.setColor(Ui.GOLD);
                    g.drawLine(baseX + 80, baseY + 24, baseX + 110, baseY + 10);
                    g.setColor(new Color(255, 255, 255, 210));
                    g.drawLine(baseX + 42, baseY + 54, baseX + 40, baseY + 60);
                    g.drawLine(baseX + 52, baseY + 54, baseX + 54, baseY + 60);
                }
                case LICANTROPO -> {
                    int[] x = {baseX + 22, baseX + 34, baseX + 42};
                    int[] y = {baseY + 22, baseY + 2, baseY + 24};
                    g.fillPolygon(x, y, 3);
                    int[] x2 = {baseX + 50, baseX + 62, baseX + 70};
                    int[] y2 = {baseY + 22, baseY + 2, baseY + 24};
                    g.fillPolygon(x2, y2, 3);
                    g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.drawLine(baseX - 8, baseY + 84, baseX + 18, baseY + 74);
                    g.drawLine(baseX + 72, baseY + 76, baseX + 100, baseY + 58);
                    g.setStroke(new BasicStroke(3f));
                    g.drawLine(baseX + 74, baseY + 20, baseX + 92, baseY - 2);
                }
                case CAZADOR -> {
                    g.fillArc(baseX + 14, baseY + 10, 62, 24, 0, 180);
                    g.setStroke(new BasicStroke(4f));
                    g.drawLine(baseX + 72, baseY + 72, baseX + 110, baseY + 34);
                    g.drawLine(baseX + 110, baseY + 34, baseX + 122, baseY + 48);
                    g.setColor(Ui.TEAL);
                    g.drawLine(baseX + 82, baseY + 58, baseX + 116, baseY + 86);
                    g.setColor(Ui.GOLD);
                    g.drawLine(baseX + 8, baseY + 82, baseX + 24, baseY + 40);
                }
            }
        }

        private String roleCopy(TipoPersonaje type) {
            return switch (type) {
                case VAMPIRO -> "Sangre y disciplina";
                case LICANTROPO -> "Rabia y transformacion";
                case CAZADOR -> "Pulso y talento";
            };
        }
    }

    private static final class JLabelPainter {
        private JLabelPainter() {
        }

        static void draw(Graphics2D g, String text, int x, int y, Color color, float size, int style) {
            if (text == null || text.isBlank()) {
                return;
            }
            g.setFont(g.getFont().deriveFont(style, size));
            g.setColor(color);
            g.drawString(text, x, y);
        }
    }
}
