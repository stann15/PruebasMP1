package es.urjc.metprog.gui;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public final class GuiMain {
    private GuiMain() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
                // The custom theme still applies if the cross-platform look and feel is unavailable.
            }
            Ui.install();
            new FantasyCombatFrame().setVisible(true);
        });
    }
}
