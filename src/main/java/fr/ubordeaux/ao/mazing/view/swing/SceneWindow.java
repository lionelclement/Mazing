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

    /**
     * Creates a SceneWindow instance.
     * @param title title value
     * @param component component value
     */
    public SceneWindow(String title, JComponent component) {
        this.title = java.util.Objects.requireNonNull(title);
        this.component = java.util.Objects.requireNonNull(component);
    }

    /**
     * Performs the show operation.
     */
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

    /**
     * Performs the close operation.
     */
    public void close() {
        SwingUtilities.invokeLater(() -> {
            if (frame != null) {
                frame.dispose();
                frame = null;
            }
        });
    }
}
