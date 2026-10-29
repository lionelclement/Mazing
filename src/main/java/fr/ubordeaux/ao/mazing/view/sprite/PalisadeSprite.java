package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometryProvider;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * A wooden palisade aligned with one edge of a diamond-shaped floor tile.
 * No dedicated fence texture is present in the original isometric atlas,
 * so these four reusable sprites are drawn once and cached as images.
 */
public enum PalisadeSprite implements Sprite, ShadowGeometryProvider {
    /** Edge at the smaller X grid coordinate. */
    X_MIN(0, -8, 128, -72),
    /** Edge at the larger X grid coordinate. */
    X_MAX(128, 56, 256, -8),
    /** Edge at the smaller Y grid coordinate. */
    Y_MIN(0, -8, 128, 56),
    /** Edge at the larger Y grid coordinate. */
    Y_MAX(128, -72, 256, -8);

    private static final int IMAGE_TOP = -176;
    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 248;
    private static final int PICKETS = 8;

    private final BufferedImage image;

    /**
     * Tests which side of this fence the actor occupies in grid coordinates.
     * An X-facing edge is a plane of constant X; a Y-facing edge has
     * constant Y. Comparing screen Y or the tile center would incorrectly
     * classify the far-side fence as foreground on an isometric platform.
     *
     * @param tileX grid X of the tile carrying this edge
     * @param tileY grid Y of the tile carrying this edge
     * @param actorX precise grid X of the actor's feet
     * @param actorY precise grid Y of the actor's feet
     * @return true when the fence lies between the viewer and the actor
     */
    public boolean isInFrontOf(double tileX, double tileY,
            double actorX, double actorY) {
        final double epsilon = 1e-6;
        return switch (this) {
            case X_MIN -> actorX < tileX - 0.5 - epsilon;
            case X_MAX -> actorX < tileX + 0.5 - epsilon;
            case Y_MIN -> actorY > tileY - 0.5 + epsilon;
            case Y_MAX -> actorY > tileY + 0.5 + epsilon;
        };
    }

    /** The stake row lies on a physical tile edge, not at its center. */
    @Override
    public ShadowGeometry shadowGeometry(Orientation orientation) {
        return switch (this) {
            case X_MIN -> ShadowGeometry.edgeX(0.0, 0.9);
            case X_MAX -> ShadowGeometry.edgeX(1.0, 0.9);
            case Y_MIN -> ShadowGeometry.edgeY(0.0, 0.9);
            case Y_MAX -> ShadowGeometry.edgeY(1.0, 0.9);
        };
    }

    PalisadeSprite(int ax, int ay, int bx, int by) {
        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        try {
            g.translate(0, -IMAGE_TOP);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Horizontal wooden crossbars are placed behind the pickets.
            drawRail(g, ax, ay, bx, by, 22);
            drawRail(g, ax, ay, bx, by, 54);

            // A continuous row of pointed, overlapping wooden stakes.
            for (int i = 0; i < PICKETS; i++) {
                double t = (i + 0.5) / PICKETS;
                double px = ax + (bx - ax) * t;
                double py = ay + (by - ay) * t;
                drawPicket(g, px, py, i);
            }
        } finally {
            g.dispose();
        }
    }

    /** Draws a shaded crossbar along the isometric tile edge. */
    private static void drawRail(Graphics2D g,
            int ax, int ay, int bx, int by, int height) {
        double y1 = ay - height;
        double y2 = by - height;
        g.setColor(new Color(66, 39, 23));
        g.setStroke(new BasicStroke(12f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.drawLine(ax, (int) y1, bx, (int) y2);
        g.setColor(new Color(132, 81, 45));
        g.setStroke(new BasicStroke(8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g.drawLine(ax, (int) y1 - 2, bx, (int) y2 - 2);
        g.setColor(new Color(178, 115, 65));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(ax, (int) y1 - 5, bx, (int) y2 - 5);
    }

    /** Draws one pointed wooden stake, with a shaded side and grain. */
    private static void drawPicket(Graphics2D g, double px, double py, int index) {
        int height = 78 + (index % 3) * 3;
        double top = py - height;
        Path2D face = new Path2D.Double();
        face.moveTo(px - 8, top + 14);
        face.lineTo(px, top);
        face.lineTo(px + 8, top + 14);
        face.lineTo(px + 8, py - 2);
        face.lineTo(px - 8, py - 2);
        face.closePath();
        g.setColor(new Color(61, 35, 22));
        g.fill(face);

        Path2D wood = new Path2D.Double();
        wood.moveTo(px - 6, top + 15);
        wood.lineTo(px, top + 4);
        wood.lineTo(px + 4, top + 15);
        wood.lineTo(px + 4, py - 4);
        wood.lineTo(px - 6, py - 4);
        wood.closePath();
        g.setColor(new Color(143 + (index % 3) * 8, 91 + (index % 3) * 5, 49));
        g.fill(wood);

        g.setColor(new Color(190, 132, 76, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine((int) px - 4, (int) top + 18, (int) px - 4, (int) py - 11);
        g.setColor(new Color(80, 46, 27, 130));
        g.drawLine((int) px + 2, (int) top + 22, (int) px + 2, (int) py - 10);
    }

    @Override
    public void render(Graphics2D g, int x, int y,
            Orientation orientation, int animationIndex, int zoom) {
        Graphics2D scaled = (Graphics2D) g.create();
        try {
            scaled.translate(x, y);
            scaled.scale(1.0 / zoom, 1.0 / zoom);
            scaled.drawImage(image, 0, IMAGE_TOP, null);
        } finally {
            scaled.dispose();
        }
    }

    @Override
    public Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
        int top = (int) Math.floor(screenY + IMAGE_TOP / (double) zoom);
        int bottom = (int) Math.ceil(screenY + (IMAGE_TOP + IMAGE_HEIGHT) / (double) zoom);
        return new Rectangle(screenX, top,
                (int) Math.ceil(IMAGE_WIDTH / (double) zoom), bottom - top);
    }

    @Override
    public int animationSize() {
        return 0;
    }
}
