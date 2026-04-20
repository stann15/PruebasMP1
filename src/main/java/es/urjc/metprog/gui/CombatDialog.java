package es.urjc.metprog.gui;

import es.urjc.metprog.domain.combat.RegistroCombate;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import java.time.format.DateTimeFormatter;
import java.util.List;

final class CombatDialog {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private CombatDialog() {
    }

    static void show(Component parent, RegistroCombate registro) {
        Window owner = SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(owner, "Resultado del combate", java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ArenaReplayPanel replay = new ArenaReplayPanel(registro);
        JPanel speedControls = Ui.transparent(new FlowLayout(FlowLayout.LEFT, 8, 0));
        speedControls.add(Ui.small("Velocidad"));
        JButton speed1 = Ui.primaryButton("x1");
        JButton speed5 = Ui.secondaryButton("x5");
        JButton speed10 = Ui.secondaryButton("x10");
        JButton[] speedButtons = {speed1, speed5, speed10};
        configureSpeedButton(speed1, speedButtons, replay, 1);
        configureSpeedButton(speed5, speedButtons, replay, 5);
        configureSpeedButton(speed10, speedButtons, replay, 10);
        markSpeed(speedButtons, speed1);
        speedControls.add(speed1);
        speedControls.add(speed5);
        speedControls.add(speed10);

        JPanel summary = Ui.transparent(new GridLayout(1, 4, 12, 12));
        summary.add(metric("Rondas", String.valueOf(registro.getRondas()), Ui.BLUE));
        summary.add(metric("Vencedor", registro.esEmpate() ? "EMPATE" : registro.getVencedor(), registro.esEmpate() ? Ui.MUTED : Ui.GOLD));
        summary.add(metric("Oro", String.valueOf(registro.getOroGanado()), Ui.GREEN));
        summary.add(metric("Fecha", FORMAT.format(registro.getFecha()), Ui.TEAL));

        JTextArea events = Ui.textArea();
        events.setText(String.join(System.lineSeparator(), registro.getEventos()));
        events.setCaretPosition(0);

        JTextArea survivors = Ui.textArea();
        survivors.setText("Supervivientes de " + registro.getNickDesafiante() + ": "
                + registro.getEsbirrosSupervivientesDesafiante() + System.lineSeparator()
                + "Supervivientes de " + registro.getNickDesafiado() + ": "
                + registro.getEsbirrosSupervivientesDesafiado());

        JButton close = Ui.primaryButton("Cerrar");
        close.addActionListener(event -> dialog.dispose());
        JPanel actions = Ui.transparent(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(close);

        JPanel root = Ui.dialogPanel(new BorderLayout(12, 12));
        JPanel title = Ui.transparent(new BorderLayout(12, 4));
        title.add(Ui.title("Combate resuelto", 26f), BorderLayout.WEST);
        title.add(speedControls, BorderLayout.EAST);
        root.add(title, BorderLayout.NORTH);
        JPanel center = Ui.transparent(new BorderLayout(12, 12));
        JPanel top = Ui.transparent(new BorderLayout(12, 12));
        top.add(replay, BorderLayout.CENTER);
        top.add(summary, BorderLayout.SOUTH);
        center.add(top, BorderLayout.NORTH);
        center.add(Ui.titled("Eventos por ronda", Ui.scroll(events)), BorderLayout.CENTER);
        center.add(Ui.titled("Esbirros supervivientes", Ui.scroll(survivors)), BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);
        root.add(actions, BorderLayout.SOUTH);

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                replay.stop();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                replay.stop();
            }
        });
        dialog.setContentPane(root);
        dialog.setSize(1080, 820);
        dialog.setLocationRelativeTo(owner);
        dialog.getRootPane().setDefaultButton(close);
        dialog.setVisible(true);
    }

    private static void configureSpeedButton(JButton button, JButton[] group, ArenaReplayPanel replay, int multiplier) {
        button.addActionListener(event -> {
            replay.setSpeed(multiplier);
            markSpeed(group, button);
        });
        button.setToolTipText("Reproducir a x" + multiplier);
    }

    private static void markSpeed(JButton[] group, JButton selected) {
        for (JButton button : group) {
            Ui.setSpeedSelected(button, button == selected);
        }
    }

    private static JPanel metric(String label, String value, Color color) {
        JPanel panel = Ui.card();
        JLabel title = Ui.small(label);
        JLabel number = Ui.title(value, 20f);
        number.setForeground(color);
        panel.add(title, BorderLayout.NORTH);
        panel.add(number, BorderLayout.CENTER);
        return panel;
    }

    private static final class ArenaReplayPanel extends JPanel {
        private final RegistroCombate registro;
        private final Timer timer;
        private int speed = 1;
        private int tick;
        private int eventIndex;

        ArenaReplayPanel(RegistroCombate registro) {
            this.registro = registro;
            setOpaque(false);
            setPreferredSize(new java.awt.Dimension(940, 340));
            timer = new Timer(700, event -> advance());
            timer.start();
        }

        void stop() {
            timer.stop();
        }

        void setSpeed(int speed) {
            this.speed = Math.max(1, speed);
            timer.setDelay(Math.max(45, 700 / this.speed));
            repaint();
        }

        private void advance() {
            List<String> events = registro.getEventos();
            if (events.isEmpty() || eventIndex >= events.size() - 1) {
                timer.stop();
                repaint();
                return;
            }
            tick++;
            eventIndex++;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();
            int padding = 18;

            g.setPaint(new GradientPaint(0, 0, new Color(6, 9, 21), w, h, new Color(46, 18, 52)));
            g.fillRoundRect(padding, padding, w - padding * 2, h - padding * 2, 22, 22);
            g.setPaint(new GradientPaint(padding, padding, Ui.CRIMSON, w - padding, h - padding, Ui.TEAL));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(padding + 1, padding + 1, w - padding * 2 - 2, h - padding * 2 - 2, 22, 22);

            drawBacklights(g, w, h);

            int arenaY = h / 2 + 42;
            g.setColor(new Color(58, 223, 201, 54));
            g.fillOval(w / 2 - 260, arenaY - 64, 520, 128);
            g.setColor(new Color(255, 194, 85, 48));
            g.drawOval(w / 2 - 295, arenaY - 82, 590, 164);
            g.setColor(new Color(255, 255, 255, 18));
            g.drawLine(42, arenaY + 74, w - 42, arenaY + 74);

            float progress = registro.getEventos().isEmpty() ? 1f : (eventIndex + 1f) / registro.getEventos().size();
            boolean leftWins = registro.getVencedor() != null && registro.getVencedor().equalsIgnoreCase(registro.getNickDesafiante());
            boolean rightWins = registro.getVencedor() != null && registro.getVencedor().equalsIgnoreCase(registro.getNickDesafiado());
            float leftHealth = health(progress, leftWins, rightWins);
            float rightHealth = health(progress, rightWins, leftWins);

            int pulse = timer.isRunning() ? (int) (Math.sin(tick * 0.8) * (10 + speed)) : 0;
            drawFighter(g, 130 + pulse, arenaY - 76, Ui.CRIMSON, registro.getNickDesafiante(), leftHealth, true);
            drawFighter(g, w - 270 - pulse, arenaY - 84, Ui.GOLD, registro.getNickDesafiado(), rightHealth, false);
            if (timer.isRunning()) {
                drawImpact(g, w / 2, arenaY - 18, progress);
            } else {
                drawVictorySeal(g, w / 2, arenaY - 18);
            }

            g.setFont(getFont().deriveFont(Font.BOLD, 18f));
            g.setColor(Ui.TEXT);
            g.drawString("Recreacion visual del combate", 42, 55);
            g.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g.setColor(Ui.MUTED);
            String status = timer.isRunning() ? "reproduciendo" : "finalizado";
            g.drawString("Evento " + Math.min(eventIndex + 1, Math.max(1, registro.getEventos().size())) + "/" + Math.max(1, registro.getEventos().size()) + "  |  velocidad x" + speed + "  |  " + status, 42, 76);
            drawProgress(g, 42, 92, w - 84, progress);

            String event = registro.getEventos().isEmpty() ? "Sin eventos registrados." : registro.getEventos().get(eventIndex);
            g.setColor(new Color(3, 6, 12, 210));
            g.fillRoundRect(42, h - 70, w - 84, 46, 12, 12);
            g.setColor(Ui.TEAL);
            g.drawRoundRect(42, h - 70, w - 84, 46, 12, 12);
            g.setColor(Ui.TEXT);
            g.setFont(getFont().deriveFont(Font.PLAIN, 13f));
            g.drawString(trim(event, 128), 56, h - 43);
            g.dispose();
        }

        private float health(float progress, boolean winner, boolean loser) {
            if (registro.esEmpate()) {
                return Math.max(0.32f, 1f - progress * 0.55f);
            }
            if (winner) {
                return Math.max(0.42f, 1f - progress * 0.28f);
            }
            if (loser) {
                return Math.max(0.04f, 1f - progress * 0.92f);
            }
            return Math.max(0.2f, 1f - progress * 0.65f);
        }

        private void drawFighter(Graphics2D g, int x, int y, Color color, String name, float health, boolean left) {
            g.setColor(new Color(255, 255, 255, 38));
            g.fillOval(x - 24, y + 132, 164, 28);
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 56));
            g.fillOval(x - 28, y - 20, 166, 182);

            g.setPaint(new GradientPaint(x + 10, y + 46, color.darker(), x + 118, y + 142, color.brighter()));
            int[] capeX = {x + 18, x + 96, x + 128, x - 12};
            int[] capeY = {y + 38, y + 36, y + 142, y + 146};
            g.fillPolygon(capeX, capeY, capeX.length);

            g.setColor(new Color(13, 17, 29));
            g.fillRoundRect(x + 30, y + 45, 70, 92, 20, 20);
            g.setPaint(new GradientPaint(x + 34, y + 48, color, x + 96, y + 132, color.darker()));
            g.fillRoundRect(x + 38, y + 54, 54, 78, 16, 16);

            g.setColor(color.brighter());
            g.fillOval(x + 37, y + 5, 58, 58);
            g.setColor(new Color(248, 238, 218));
            g.fillOval(x + 47, y + 17, 38, 34);
            g.setColor(new Color(5, 8, 15));
            g.fillOval(x + (left ? 63 : 57), y + 30, 5, 5);
            g.fillOval(x + (left ? 76 : 70), y + 30, 5, 5);
            g.setColor(color.darker());
            g.fillArc(x + 43, y + 8, 46, 24, 0, 180);

            g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(color.brighter());
            g.drawLine(x + (left ? 88 : 40), y + 72, x + (left ? 142 : -10), y + 36);
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(Ui.TEXT);
            if (left) {
                g.drawLine(x + 126, y + 20, x + 152, y + 60);
                g.drawLine(x + 123, y + 25, x + 139, y + 16);
            } else {
                g.drawLine(x - 2, y + 22, x - 28, y + 62);
                g.drawLine(x - 4, y + 26, x - 20, y + 16);
            }

            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(24, 29, 45));
            g.drawLine(x + 50, y + 134, x + 26, y + 164);
            g.drawLine(x + 82, y + 134, x + 112, y + 164);
            g.setColor(color);
            g.fillRoundRect(x + (left ? 94 : 8), y + 78, 30, 38, 10, 10);
            g.setColor(new Color(255, 255, 255, 70));
            g.drawRoundRect(x + (left ? 94 : 8), y + 78, 30, 38, 10, 10);

            g.setColor(Ui.TEXT);
            g.setFont(getFont().deriveFont(Font.BOLD, 13f));
            g.drawString(name, x - 10, y - 12);
            drawHealth(g, x - 8, y - 2, 140, health, color);
        }

        private void drawHealth(Graphics2D g, int x, int y, int width, float health, Color color) {
            g.setColor(new Color(3, 6, 12, 210));
            g.fillRoundRect(x, y, width, 8, 8, 8);
            g.setColor(color);
            g.fillRoundRect(x, y, Math.max(4, (int) (width * health)), 8, 8, 8);
        }

        private void drawImpact(Graphics2D g, int x, int y, float progress) {
            int radius = 24 + speed + (int) (Math.sin(tick * 0.8) * 9);
            g.setStroke(new BasicStroke(3f));
            g.setColor(new Color(255, 194, 85, 165));
            g.drawOval(x - radius, y - radius, radius * 2, radius * 2);
            g.setColor(new Color(58, 223, 201, 135));
            g.drawLine(x - 34, y + 30, x + 34, y - 30);
            g.setColor(new Color(232, 72, 101, 135));
            g.drawLine(x - 34, y - 30, x + 34, y + 30);
        }

        private void drawVictorySeal(Graphics2D g, int x, int y) {
            g.setColor(new Color(3, 6, 12, 210));
            g.fillRoundRect(x - 84, y - 28, 168, 48, 18, 18);
            g.setColor(Ui.GOLD);
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(x - 84, y - 28, 168, 48, 18, 18);
            g.setFont(getFont().deriveFont(Font.BOLD, 16f));
            g.setColor(Ui.TEXT);
            String text = registro.esEmpate() ? "EMPATE" : "VICTORIA";
            int width = g.getFontMetrics().stringWidth(text);
            g.drawString(text, x - width / 2, y + 2);
        }

        private void drawBacklights(Graphics2D g, int w, int h) {
            for (int i = 0; i < 6; i++) {
                int alpha = 22 + i * 5;
                g.setColor(new Color(255, 194, 85, alpha));
                g.drawLine(70 + i * 55, 102, w / 2 - 45 + i * 12, h - 94);
                g.setColor(new Color(58, 223, 201, alpha));
                g.drawLine(w - 70 - i * 55, 102, w / 2 + 45 - i * 12, h - 94);
            }
        }

        private void drawProgress(Graphics2D g, int x, int y, int width, float progress) {
            g.setColor(new Color(3, 6, 12, 220));
            g.fillRoundRect(x, y, width, 8, 8, 8);
            g.setPaint(new GradientPaint(x, y, Ui.CRIMSON, x + width, y, Ui.TEAL));
            g.fillRoundRect(x, y, Math.max(8, (int) (width * progress)), 8, 8, 8);
        }

        private String trim(String text, int max) {
            return text.length() <= max ? text : text.substring(0, max - 3) + "...";
        }
    }
}
