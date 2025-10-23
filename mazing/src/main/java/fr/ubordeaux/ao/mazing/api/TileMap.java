package fr.ubordeaux.ao.mazing.api;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

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

    public static void drawImage(
            float x, float y, float z,
            BufferedImage image, Graphics g,
            RenderType renderType, float alpha,
            float brightness, // 0.0 (noir) → 1.0 (très lumineux)
            Color color) {
        // Projection isométrique
        int anchorY = (renderType == RenderType.FLOOR)
                ? tileHeight / 2
                : tileHeight / 2 - 8;

        Point2D p = gridToScreen(x, y, z);
        int screenX = (int) p.getX();
        int screenY = (int) p.getY();

        Graphics2D g2d = (Graphics2D) g;

        // Alpha principal
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        int ix = screenX - image.getWidth() / 2;
        int iy = screenY - image.getHeight() + anchorY;
        int dx = ix - originX;
        int dy = iy - originY;

        // Vérifie si dans la fenêtre visible
        if (Math.sqrt(dx * dx + dy * dy) < (2 * originX)) {
            
            // --- Correction de luminosité simple via transparence ---
            if (brightness != 0f) {

                float factor = Math.max(0f, Math.min(brightness, 1f));
                RescaleOp op = new RescaleOp(
                        new float[] { factor, factor, factor, 1f }, // R,G,B,A
                        new float[] { 0f, 0f, 0f, 0f }, // offset
                        null);
                BufferedImage brightImage = op.filter(image, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                //g2d.drawImage(brightImage, ix, iy, null);
                image = brightImage;

            }

            if (color != null) {

                image = tintByLuminosity(image, color);

            }

            g2d.drawImage(image, ix, iy, null);


        }
    }

    /**
	 * Mélange une couleur sur tous les pixels opaques, proportionnellement à leur
	 * luminosité.
	 */
	public static BufferedImage tintByLuminosity(BufferedImage img, Color targetColor) {
		int width = img.getWidth();
		int height = img.getHeight();
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		int rT = targetColor.getRed();
		int gT = targetColor.getGreen();
		int bT = targetColor.getBlue();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = img.getRGB(x, y);
				int alpha = (argb >> 24) & 0xff;

				// Ne jamais toucher les pixels totalement transparents
				if (alpha == 0) {
					result.setRGB(x, y, argb);
					continue;
				}

				int red = (argb >> 16) & 0xff;
				int green = (argb >> 8) & 0xff;
				int blue = argb & 0xff;

				// Luminosité relative
				float lum = (0.299f * red + 0.587f * green + 0.114f * blue) / 255f;

				// On applique la couleur proportionnellement à la luminosité
				float factor = lum; // 0 (noir) -> 1 (blanc)
				red = (int) (red * (1 - factor) + rT * factor);
				green = (int) (green * (1 - factor) + gT * factor);
				blue = (int) (blue * (1 - factor) + bT * factor);

				red = Math.min(255, Math.max(0, red));
				green = Math.min(255, Math.max(0, green));
				blue = Math.min(255, Math.max(0, blue));

				result.setRGB(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
			}
		}

		return result;
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
