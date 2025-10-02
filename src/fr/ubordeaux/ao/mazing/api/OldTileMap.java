package fr.ubordeaux.ao.mazing.api;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class OldTileMap {
    private final int tileWidth;
    private final int tileHeight;
    private int originX;
    private int originY;
    private static OldTileMap instance;
    // Facteur vertical pour dimétrie (1/2 = isométrie classique, 1/3 = dimétrie)
    private static float verticalScale = 1f / 3f;

    private OldTileMap(int tileWidth, int tileHeight, int windowWidth, int windowHeight, int cols, int rows) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        originX = 0;
        originY = windowHeight / 4;
    }

    public static OldTileMap getInstance(int tileWidth, int tileHeight, int windowWidth, int windowHeight, int cols,
            int rows) {
        if (instance == null) {
            instance = new OldTileMap(tileWidth, tileHeight, windowWidth, windowHeight, cols, rows);
        }
        return instance;
    }

    public static OldTileMap getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TileMap instance not initialized");
        }
        return instance;
    }

    /**
     * Convertit des coordonnées grille + hauteur en coordonnées écran isométriques.
     * 
     * @param col  colonne de la grille
     * @param row  ligne de la grille
     * @param step nombre de tuiles empilées (hauteur)
     * @return point écran
     */
    public Point2D gridToScreen(float col, float row, float step, boolean origin) {

        int originX = origin ? this.originX : 0;
        int originY = origin ? this.originY : 0;

        // Projection isométrique
        // float isoX = originX + (col - row) * tileWidth / 2;
        // float isoY = originY + (col + row) * tileHeight / 4 - step * tileHeight *
        // 0.60f;

        // Projection dimétrique
        float isoX = originX + (col - row) * tileWidth / 2f;
        float isoY = originY + (col + row) * (tileHeight * verticalScale)
                - step * tileHeight * 0.60f;

        return new Point2D.Float(isoX, isoY);
    }

    public void drawImage(float x, float y, float z,
            BufferedImage image, Graphics g,
            int offsetX, int offsetY, RenderType renderType) {

        // Projection isométrique
        // int anchorY = (renderType == RenderType.FLOOR) ? tileHeight / 2 : tileHeight
        // / 2 - 8;

        // Projection dimétrique
        int scaledTileHeight = (int) (tileHeight * verticalScale);
        int anchorY = (renderType == RenderType.FLOOR)
                ? scaledTileHeight
                : scaledTileHeight - 8;

        Point2D p = gridToScreen(x, y, z, true);
        int screenX = (int) p.getX() + offsetX;
        int screenY = (int) p.getY() + offsetY;
        g.drawImage(image,
                screenX - image.getWidth() / 2,
                screenY - image.getHeight() + anchorY,
                null);
    }

    /** Dessine une tuile isométrique 3D */
    public void drawTile(int col, int row, int step, Graphics g, Color color) {
        Point2D p = gridToScreen(col, row, step, true);

        // isométrique
        // int halfW = tileWidth / 2;
        // int quarterH = tileHeight / 4;
        // int[] xPoints = { (int) p.getX(), (int) p.getX() + halfW, (int) p.getX(),
        // (int) p.getX() - halfW };
        // int[] yPoints = { (int) p.getY(), (int) p.getY() + quarterH, (int) p.getY() +
        // tileHeight / 2,
        // (int) p.getY() + quarterH };

        int halfW = tileWidth / 2;
        int halfH = (int) (tileHeight * verticalScale); // sommet vertical ajusté

        int[] xPoints = {
                (int) p.getX(),
                (int) p.getX() + halfW,
                (int) p.getX(),
                (int) p.getX() - halfW
        };

        int[] yPoints = {
                (int) p.getY(),
                (int) p.getY() + halfH,
                (int) p.getY() + 2 * halfH,
                (int) p.getY() + halfH
        };

        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(color);
        g2d.fillPolygon(xPoints, yPoints, 4);
    }

    /** Dessine un point (utilisé pour le survol) */
    public void drawPoint(int col, int row, int step, Graphics g, Color color) {
        Point2D p = gridToScreen(col, row, step, true);
        g.setColor(color);
        g.fillOval((int) p.getX() - 2, (int) p.getY() - 2, 4, 4);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }
}
