package fr.ubordeaux.ao.mazing;

import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import fr.ubordeaux.ao.mazing.view.shadow.DirectionalLight;
import fr.ubordeaux.ao.mazing.view.swing.ViewPanel;
import fr.ubordeaux.ao.mazing.view.swing.SceneWindow;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.Timer;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Public API for an isometric scene.
 *
 * The map is centered automatically. The user may subsequently
 * move the scene without changing world coordinates.
 */
public final class Scene {

    private final ViewPanel panel;
    private final IsometricRenderer renderer;
    private final int mapWidth;
    private final int mapHeight;

    private int offsetX;
    private int offsetY;
    private final SceneWindow window;

    private Timer animationTimer;

    /**
     * Creates a Scene instance.
     * @param width width value
     * @param height height value
     * @param mapWidth map width value
     * @param mapHeight map height value
     */
    public Scene(int width, int height, int mapWidth, int mapHeight) {
        if (width <= 0 || height <= 0
                || mapWidth <= 0 || mapHeight <= 0) {
            throw new IllegalArgumentException(
                    "Scene and map dimensions must be positive");
        }

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        renderer = new IsometricRenderer(0, 0);
        panel = new ViewPanel(width, height, renderer);
        panel.setPreferredSize(new Dimension(width, height));
        window = new SceneWindow("Mazing", panel);

        updateOrigin(width, height);

        panel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent event) {
                updateOrigin(panel.getWidth(), panel.getHeight());
            }
        });
    }

    /**
     * Centers the geometric map using the actual projection.
     * Camera offsets are preserved.
     */
    private void updateOrigin(int width, int height) {
        renderer.setOrigin(0, 0);

        var center = renderer.projectWorld(
                mapWidth / 2.0,
                mapHeight / 2.0,
                0);

        renderer.setOrigin(
                width / 2 - center.x() + offsetX,
                height / 2 - center.y() + offsetY);

        panel.repaint();
    }

    private int currentWidth() {
        return panel.getWidth() > 0
                ? panel.getWidth()
                : panel.getPreferredSize().width;
    }

    private int currentHeight() {
        return panel.getHeight() > 0
                ? panel.getHeight()
                : panel.getPreferredSize().height;
    }

    /**
     * Moves the scene in screen pixels.
     * @param dx dx value
     * @param dy dy value
     */
    public void moveScene(int dx, int dy) {
        offsetX += dx;
        offsetY += dy;
        renderer.pan(dx, dy);
        panel.repaint();
    }

    /** Resets camera offsets and centers the map. */
    public void centerMap() {
        offsetX = 0;
        offsetY = 0;
        updateOrigin(currentWidth(), currentHeight());
    }

    /**
     * Changes zoom while preserving camera displacement.
     * @param zoom rendering zoom level
     */
    public void setZoom(int zoom) {
        renderer.setZoom(zoom);
        updateOrigin(currentWidth(), currentHeight());
    }

    /**
     * Updates the debug.
     * @param enabled enabled value
     */
    public void setDebug(boolean enabled) {
        renderer.setDebug(enabled);
        panel.repaint();
    }

    /**
     * Performs the addRenderable operation.
     * @param renderable renderable value
     * @return the resulting value
     */
    public boolean addRenderable(Renderable renderable) {
        boolean added = panel.addRenderable(renderable);
        panel.repaint();
        return added;
    }

    /**
     * Performs the addRenderables operation.
     * @param renderables renderables value
     * @return the resulting value
     */
    public boolean addRenderables(List<Renderable> renderables) {
        boolean added = panel.addRenderables(renderables);
        panel.repaint();
        return added;
    }

    /**
     * Performs the removeRenderable operation.
     * @param renderable renderable value
     * @return the resulting value
     */
    public boolean removeRenderable(Renderable renderable) {
        boolean removed = panel.removeRenderable(renderable);
        panel.repaint();
        return removed;
    }

    /**
     * Performs the clearRenderables operation.
     */
    public void clearRenderables() {
        panel.clearRenderables();
        panel.repaint();
    }

    /**
     * Performs the addDebugCube operation.
     * @param x horizontal coordinate
     * @param y vertical coordinate
     * @param z height coordinate
     */
    public void addDebugCube(double x, double y, double z) {
        panel.addDebugCube(x, y, z);
    }

    /**
     * Updates the staticshadowsenabled.
     * @param enabled enabled value
     */
    public void setStaticShadowsEnabled(boolean enabled) {
        panel.setStaticShadowsEnabled(enabled);
    }

    /**
     * Updates the staticshadowlight.
     * @param light light value
     */
    public void setStaticShadowLight(DirectionalLight light) {
        panel.setStaticShadowLight(light);
    }

    /**
     * Performs the prepareStaticShadows operation.
     */
    public void prepareStaticShadows() {
        panel.prepareStaticShadows();
    }

    /**
     * Performs the invalidateStaticShadows operation.
     */
    public void invalidateStaticShadows() {
        panel.invalidateStaticShadows();
    }

    /**
     * Performs the animationTick operation.
     */
    public void animationTick() {
        panel.animationTick();
    }

    /**
     * Performs the repaint operation.
     */
    public void repaint() {
        panel.repaint();
    }

    /**
     * Optional Swing integration point.
     * @return the resulting value
     */
    public JPanel getPanel() {
        return panel;
    }

    /** Displays the scene in its own window. */
    public void show() {
        window.show();
    }

    /** Closes the window created by show(). */
    public void close() {
        stopAnimation();
        window.close();
    }

    /**
     * Starts or restarts the animation loop.
     *
     * @param fps animation updates per second, strictly positive
     */
    public void startAnimation(int fps) {
        if (fps <= 0) {
            throw new IllegalArgumentException("fps must be positive");
        }

        SwingUtilities.invokeLater(() -> {
            stopAnimationInternal();

            int delay = Math.max(1, 1000 / fps);

            animationTimer = new Timer(delay, event -> {
                panel.animationTick();
                panel.repaint();
            });

            animationTimer.start();
        });
    }

    /** Stops the animation loop. */
    public void stopAnimation() {
        SwingUtilities.invokeLater(this::stopAnimationInternal);
    }

    private void stopAnimationInternal() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }
    }
}
