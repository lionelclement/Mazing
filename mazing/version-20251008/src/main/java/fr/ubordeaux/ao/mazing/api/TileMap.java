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
public class TileMap {
    static int tileWidth;
    static int tileHeight;
    static int originX;
    static int originY;
    private static TileMap instance;
    private static int scrollX;
    private static int scrollY;

    private TileMap(int tileWidth, int tileHeight, int originX, int originY) {

        TileMap.tileWidth = tileWidth;
        TileMap.tileHeight = tileHeight;
        TileMap.originX = originX;
        TileMap.originY = originY;

    }

    public static void initInstance(int tileWidth, int tileHeight, int originX, int originY) {
        instance = new TileMap(tileWidth, tileHeight, originX, originY);
    }

    public static void initInstance(int tileWidth, int tileHeight) {
        instance = new TileMap(tileWidth, tileHeight, originX, originY);
    }

    public static TileMap getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TileMap instance not initialized");
        }
        return instance;
    }

    public static Point2D gridToScreen(float col, float row, float step) {
        float x = col + scrollX;
        float y = row + scrollY;

        float isoX = originX + scrollX + (x - y) * (tileWidth / 2.0f);
        float isoY = originY + scrollY + (x + y) * (tileHeight / 4.0f) - step * (tileWidth / 2.0f);
        return new Point2D.Float(isoX, isoY);
    }

    public Point2D screenToGrid(float isoX, float isoY) {
        // float x = isoX - originX;
        // float y = isoY - originY;
        float x = isoX;
        float y = isoY;
        float col = (x / (tileWidth / 2.0f) + y / (tileHeight / 4.0f)) / 2.0f;
        float row = (y / (tileHeight / 4.0f) - x / (tileWidth / 2.0f)) / 2.0f;
        return new Point2D.Float(col, row);
    }

    public void drawImage(float x, float y, float z, BufferedImage image, Graphics g, int offsetX, int offsetY,
            RenderType renderType, float alpha) {

        // Projection isométrique
        int anchorY = (renderType == RenderType.FLOOR) ? tileHeight / 2
                : tileHeight
                        / 2 - 8;

        Point2D p = gridToScreen(x, y, z);
        int screenX = (int) p.getX() + offsetX;
        int screenY = (int) p.getY() + offsetY;

        // Cast Graphics en Graphics2D pour pouvoir gérer alpha
        Graphics2D g2d = (Graphics2D) g;

        // Définir l'alpha
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g.drawImage(image,
                screenX - image.getWidth() / 2,
                screenY - image.getHeight() + anchorY,
                null);
    }

    /** Dessine une tuile isométrique 3D */
    public void drawTile(int x, int y, int z, Graphics g, Color color) {
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
    public void drawPoint(int x, int y, int z, Graphics g, Color color) {
        Point2D p = gridToScreen(x, y, z);
        g.setColor(color);
        g.fillOval((int) p.getX() - 2, (int) p.getY() - 2, 4, 4);
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public static void scroll(int scrollX, int scrollY) {
        TileMap.scrollX = scrollX;
        TileMap.scrollY = scrollY;
    }
}
