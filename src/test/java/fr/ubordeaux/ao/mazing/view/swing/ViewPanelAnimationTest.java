package fr.ubordeaux.ao.mazing.view.swing;

import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;
import org.junit.jupiter.api.Test;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewPanelAnimationTest {

    @Test
    void animationTicksAdvanceObjectsIndependentlyOfPaintingAndVisibility() {
        ViewPanel panel = new ViewPanel(80, 60);
        panel.setSize(80, 60);

        RecordingRenderable visible = new RecordingRenderable(false);
        RecordingRenderable offScreen = new RecordingRenderable(true);
        panel.addRenderable(visible);
        panel.addRenderable(offScreen);

        BufferedImage image = new BufferedImage(80, 60, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            panel.paint(graphics);
            panel.paint(graphics);
            assertEquals(0, visible.updates);
            assertEquals(0, offScreen.updates);

            panel.animationTick();
            assertEquals(1, visible.updates);
            assertEquals(1, offScreen.updates);
            assertEquals(1, visible.lastTick);

            panel.paint(graphics);
            panel.paint(graphics);
            assertEquals(1, visible.updates);
            assertEquals(1, offScreen.updates);

            panel.animationTick();
            assertEquals(2, visible.updates);
            assertEquals(2, offScreen.updates);
        } finally {
            graphics.dispose();
        }
    }

    private static final class RecordingRenderable implements Renderable {
        private final boolean offScreen;
        private int updates;
        private int lastTick = -1;

        RecordingRenderable(boolean offScreen) {
            this.offScreen = offScreen;
        }

        @Override
        public void updateAnimation(int animationTick) {
            updates++;
            lastTick = animationTick;
        }

        @Override
        public Rectangle getScreenBounds(int x, int y, int zoom) {
            return offScreen ? new Rectangle(10_000, 10_000, 10, 10) : null;
        }

        @Override
        public void renderInScreen(Graphics2D graphics, int x, int y,
                int tick, int zoom) { }

        @Override
        public void setSprite(Sprite sprite) { }

        @Override
        public int getX() { return 0; }

        @Override
        public int getY() { return 0; }

        @Override
        public int getZ() { return 0; }
    }
}
