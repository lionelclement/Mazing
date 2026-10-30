package fr.ubordeaux.ao.mazing.view.swing;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Swing window lifecycle for a Mazing scene.
 * Scene users do not need to construct a JFrame.
 */
public final class SceneWindow {
    private final String title;
    private final JComponent component;
    private JFrame frame;

    public SceneWindow(String title, JComponent component) {
        this.title = java.util.Objects.requireNonNull(title);
        this.component = java.util.Objects.requireNonNull(component);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            if (frame == null) {
                frame = new JFrame(title);
                frame.setDefaultCloseOperation(
                        JFrame.DISPOSE_ON_CLOSE);
                frame.setContentPane(component);
                frame.pack();
                frame.setLocationRelativeTo(null);
            }
            frame.setVisible(true);
        });
    }

    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
                frame = null;
            }
        });
    }
}
