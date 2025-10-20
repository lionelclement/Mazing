package fr.ubordeaux.ao.mazing.api;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Gestionnaire des tuiles du plateau de jeu en affichage isométrique.
 * Permet de dessiner des tuiles, des images 3D et de gérer la position et la
 * profondeur iso du rendu.
 */
class TileMap {
    static int tileWidth;
    static int tileHeight;
    static int originX;
    static int originY;
    static int scrollX;
    static int scrollY;

    public static void init(int tileSize, int originX, int originY) {
        TileMap.tileWidth = tileSize;
        TileMap.tileHeight = (int) (tileSize * Math.tan(Math.toRadians(26)) * 2);
        TileMap.originX = originX;
        TileMap.originY = originY;
    }

    public static Point2D gridToScreen(float col, float row, float step) {
        float x = col + scrollX;
        float y = row + scrollY;
        float isoX = originX + scrollX + (x - y) * (tileWidth / 2.0f);
        float isoY = originY + scrollY + (x + y) * (tileHeight / 4.0f) - step * (tileWidth / 2.0f);
        return new Point2D.Float(isoX, isoY);
    }

    public static Point2D screenToGrid(float isoX, float isoY) {
        float x = isoX - originX - scrollX;
        float y = isoY - originY - scrollY;

        float col = (x / (tileWidth / 2.0f) + y / (tileHeight / 4.0f)) / 2.0f;
        float row = (y / (tileHeight / 4.0f) - x / (tileWidth / 2.0f)) / 2.0f;

        return new Point2D.Float(col, row);
    }

public static void drawImage(float x, float y, float z, BufferedImage image, Graphics g,
            RenderType renderType, float alpha) {

        // Projection isométrique
        int anchorY = (renderType == RenderType.FLOOR) ? tileHeight / 2
                : tileHeight
                        / 2 - 8;

        Point2D p = gridToScreen(x, y, z);
        int screenX = (int) p.getX();
        int screenY = (int) p.getY();

        // Cast Graphics en Graphics2D pour pouvoir gérer alpha
        Graphics2D g2d = (Graphics2D) g;

        // Définir l'alpha
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int ix = screenX - image.getWidth() / 2;
        int iy = screenY - image.getHeight() + anchorY;
        int dx = ix - originX;
        int dy = iy - originY;
        // on prend une fenêtre pas plus large que width
        if (Math.sqrt(dx * dx + dy * dy) < (2 * originX)) {
            g.drawImage(image, ix, iy, null);
        }
    }

    /** Dessine une tuile isométrique 3D */
    public static void drawTile(int x, int y, int z, Graphics g, Color color) {
        Point2D p = gridToScreen(x, y, z);

        // isométrique
        int halfW = tileWidth / 2;
        int quarterH = tileHeight / 4;
        int[] xPoints = { (int) p.getX(), (int) p.getX() + halfW, (int) p.getX(),
                (int) p.getX() - halfW };
        int[] yPoints = { (int) p.getY(), (int) p.getY() + quarterH, (int) p.getY() +
                tileHeight / 2,
                (int) p.getY() + quarterH };

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    /** Dessine un point (utilisé pour le survol) */
    public static void drawPoint(int x, int y, int z, Graphics g, Color color) {
        Point2D p = gridToScreen(x, y, z);
        g.setColor(color);
        g.fillOval((int) p.getX() - 2, (int) p.getY() - 2, 4, 4);
    }

    /** Dessine un cube isométrique de la taille d'une tuile */
    /** Dessine un cube isométrique de la taille d'une tuile */
    public static void drawCube(int x, int y, int z, Graphics g, Color color) {
        Graphics2D g2d = (Graphics2D) g;

        int halfW = tileWidth / 2;
        int quarterH = tileHeight / 4;

        // Sommet haut du cube
        Point2D top = gridToScreen(x, y, z + 1);

        // Base du cube (au niveau de la tuile)
        Point2D base = gridToScreen(x, y, z);

        int topX = (int) top.getX();
        int topY = (int) top.getY();
        int baseX = (int) base.getX();
        int baseY = (int) base.getY();

        // Face du dessus
        int[] topFaceX = { topX, topX + halfW, topX, topX - halfW };
        int[] topFaceY = { topY, topY + quarterH, topY + 2 * quarterH, topY + quarterH };

        // Face gauche
        int[] leftFaceX = { topX - halfW, topX, baseX, baseX - halfW };
        int[] leftFaceY = { topY + quarterH, topY + 2 * quarterH, baseY + 2 * quarterH, baseY + quarterH };

        // Face droite
        int[] rightFaceX = { topX, topX + halfW, baseX + halfW, baseX };
        int[] rightFaceY = { topY + 2 * quarterH, topY + quarterH, baseY + quarterH, baseY + 2 * quarterH };

        // Dessiner les faces
        g2d.setColor(color.brighter());
        g2d.fillPolygon(topFaceX, topFaceY, 4);

        g2d.setColor(color.darker());
        g2d.fillPolygon(leftFaceX, leftFaceY, 4);

        g2d.setColor(color);
        g2d.fillPolygon(rightFaceX, rightFaceY, 4);

        // Contours
        g2d.setColor(Color.BLACK);
        g2d.drawPolygon(topFaceX, topFaceY, 4);
        g2d.drawPolygon(leftFaceX, leftFaceY, 4);
        g2d.drawPolygon(rightFaceX, rightFaceY, 4);
    }

    public static int getTileWidth() {
        return tileWidth;
    }

    public static int getTileHeight() {
        return tileHeight;
    }

    public static void scroll(int scrollX, int scrollY) {
        TileMap.scrollX = scrollX;
        TileMap.scrollY = scrollY;
    }

    public static int distance(AbstractRenderable ar) {
        Point2D p = TileMap.gridToScreen(ar.getX(), ar.getY(), ar.getZ());
        int dx = (int) (p.getX() - originX);
        int dy = (int) (p.getY() - originY);
        return (int) Math.sqrt(dx * dx + dy * dy);
    }
}
