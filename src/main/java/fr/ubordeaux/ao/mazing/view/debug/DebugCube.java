package fr.ubordeaux.ao.mazing.view.debug;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.renderer.IsometricRenderer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

/** A blue, one-world-cell wireframe cube for debug visualization. */
public final class DebugCube {
    private final double x;
    private final double y;
    private final double z;

    public DebugCube(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** Draws all twelve edges using the renderer's projection and current zoom. */
    public void paint(Graphics2D graphics, IsometricRenderer renderer) {
        Point[] corners = new Point[8];
        for (int i = 0; i < corners.length; i++) {
            corners[i] = renderer.projectWorld(
                    x + (i & 1), y + ((i >> 1) & 1), z + ((i >> 2) & 1));
        }

        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setColor(Color.BLUE);
            g.setStroke(new BasicStroke(2.0f));
            for (int i = 0; i < corners.length; i++) {
                for (int axis = 0; axis < 3; axis++) {
                    int bit = 1 << axis;
                    if ((i & bit) == 0) {
                        Point a = corners[i];
                        Point b = corners[i | bit];
                        g.drawLine(a.x(), a.y(), b.x(), b.y());
                    }
                }
            }
        } finally {
            g.dispose();
        }
    }
}
