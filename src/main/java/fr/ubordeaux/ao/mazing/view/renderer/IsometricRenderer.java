package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Represents IsometricRenderer in the rendering system.
 */
public class IsometricRenderer implements Renderer {
	private static final int TILE_DIAGONAL_X = 256;
	private static final int TILE_DIAGONAL_Y = 128;
	private static final int TILE_HEIGHT = TILE_DIAGONAL_Y;
	private int origX;
	private int origY;
	private final int tileDiagX;
	private final int tileDiagY;
	private final int halfTileDiagX;
	private final int halfTileDiagY;
	private final int tileHeight;
	private boolean debug;
	private int zoom;

	/**
	 * Creates a IsometricRenderer instance.
	 * @param origX orig x value
	 * @param origY orig y value
	 */
	public IsometricRenderer(int origX, int origY) {
		int tileDiagX = TILE_DIAGONAL_X;
		int tileDiagY = TILE_DIAGONAL_Y;
		int tileHeight = TILE_HEIGHT;
		this.origX = origX;
		this.origY = origY;
		this.tileDiagX = tileDiagX;
		this.tileDiagY = tileDiagY;
		this.halfTileDiagX = tileDiagX / 2;
		this.halfTileDiagY = tileDiagY / 2;
		this.tileHeight = tileHeight;
		this.debug = true;
		this.zoom = 2;
	}

	@Override
	public Point renderInIsoGrid(
            Graphics2D graphics,
            Renderable renderable,
            int animationTick) {

        Point projected = project(renderable);
        int screenX = projected.x();
        int screenY = projected.y();
        double gridX = renderable.getPreciseX();
        double gridY = renderable.getPreciseY();

        // Keep animation state and frame callbacks current even off-screen.
        renderable.updateAnimation(animationTick);
        java.awt.Rectangle visibleArea = graphics.getClipBounds();
        java.awt.Rectangle spriteBounds =
                renderable.getScreenBounds(screenX, screenY, zoom);
        if (visibleArea != null && spriteBounds != null
                && !spriteBounds.intersects(visibleArea)) {
            return new Point(screenX, screenY);
        }

        renderable.renderInScreen(
                graphics, screenX, screenY, animationTick, zoom);

        if (debug) {
            String coordinates = String.format(
                    "(%.2f, %.2f, %d)",
                    gridX, gridY, renderable.getZ());

            Rectangle2D textBounds =
                    graphics.getFont().getStringBounds(
                            coordinates,
                            graphics.getFontRenderContext());

            int labelX = (int) Math.round(
                    screenX + tileDiagX / (double) zoom / 2
                    - textBounds.getWidth() / 2);

            int labelY = (int) Math.round(
                    screenY - textBounds.getHeight() / 2);

            graphics.drawString(coordinates, labelX, labelY);
        }

        return new Point(screenX, screenY);
    }

	/**
	 * Projects world coordinates: +X is east (screen down-right), +Y is north
	 * (screen up-right), and +Z is up. The source tile's lower-left anchor
	 * accounts for the constant -1 in the vertical projection.
	 * @param x horizontal coordinate
	 * @param y vertical coordinate
	 * @param z height coordinate
	 * @return the resulting value
	 */
	public Point projectWorld(double x, double y, double z) {
		double screenX = origX + (halfTileDiagX / (double) zoom) * (x + y);
		// East (+X) goes down-right; north (+Y) goes up-right.
		double screenY = origY + (halfTileDiagY / (double) zoom)
				* (x - y - 1) - z * (tileHeight / (double) zoom);
		return new Point((int) Math.round(screenX), (int) Math.round(screenY));
	}

	/**
	 * Returns the same screen anchor used by the drawing code.
	 * @param renderable renderable value
	 * @return the resulting value
	 */
	public Point project(Renderable renderable) {
		return projectWorld(renderable.getPreciseX(), renderable.getPreciseY(),
				renderable.getPreciseZ());
	}

	@Override
	public void renderInScreen(Graphics2D g, Renderable renderable, int tick) {
		renderable.renderInScreen(g, renderable.getX(), renderable.getY(), tick, zoom);
	}

	@Override
	public void setOrigin(int originX, int originY) {
		this.origX = originX;
		this.origY = originY;
	}

	/**
	 * Moves the view in screen pixels without moving scene objects.
	 *
	 * @param deltaX horizontal camera offset in pixels
	 * @param deltaY vertical camera offset in pixels
	 */
	public void pan(int deltaX, int deltaY) {
		this.origX += deltaX;
		this.origY += deltaY;
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Whether the renderer draws coordinate labels over scene sprites.
	 * @return the resulting value
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Width of one floor tile in atlas pixels.
	 * @return the resulting value
	 */
	public int getTileDiagonalX() {
		return tileDiagX;
	}

	/**
	 * Height of one floor tile in atlas pixels.
	 * @return the resulting value
	 */
	public int getTileDiagonalY() {
		return tileDiagY;
	}

	/**
	 * Screen-vertical height of one world level in atlas pixels.
	 * @return the resulting value
	 */
	public int getTileHeight() {
		return tileHeight;
	}

	/**
	 * Returns the zoom.
	 * @return the resulting value
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Updates the zoom.
	 * @param zoom rendering zoom level
	 */
	public void setZoom(int zoom) {
		if (zoom <= 0) {
			throw new IllegalArgumentException("zoom must be positive");
		}
		this.zoom = zoom;
	}
}
