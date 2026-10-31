package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/// A Debug renderer. Not to be used outside the API.
/// Made public due to technical limitation.
/** Documents this public type or member. */
public class DebugIsometricRenderer implements Renderer {
	private int origX;
	private int origY;
	private final int tileDiagX;
	private final int tileDiagY;
	private final int halfTileDiagX;
	private final int halfTileDiagY;
	private final int tileHeight;
	private int zoom;
	private boolean debug;

	/**
	 * Creates an instance of DebugIsometricRenderer.
	  * @param origX origX value
	  * @param origY origY value
	  * @param tileDiagX tileDiagX value
	  * @param tileDiagY tileDiagY value
	  * @param tileHeight tileHeight value
	 */
	public DebugIsometricRenderer(int origX, int origY, int tileDiagX, int tileDiagY, int tileHeight) {
		this.origX = origX;
		this.origY = origY;
		this.tileDiagX = tileDiagX;
		this.tileDiagY = tileDiagY;
		this.halfTileDiagX = tileDiagX / 2;
		this.halfTileDiagY = tileDiagY / 2;
		this.tileHeight = tileHeight;
		zoom = 2;
		debug = false;
	}

	@Override
	public Point renderInIsoGrid(Graphics2D g, Renderable renderable, int tick) {
		int dx = halfTileDiagX/zoom * (renderable.getX() + renderable.getY());
		int dy = halfTileDiagY/zoom * (renderable.getX() - renderable.getY() - 1) - renderable.getZ() * (this.tileHeight/zoom);
		int y = dy / (this.tileDiagY/zoom);
		int x = dx / (this.tileDiagX/zoom);
		Point p = new Point(this.origX + dx + 18 * x, this.origY + dy + 180/zoom * y);
		renderable.renderInScreen(g, p.x(), p.y(), 0, zoom);
		if (debug) {
			var str = String.format("(%d, %d, %d)", renderable.getX(), renderable.getY(), renderable.getZ());
			Rectangle2D bound = g.getFont().getStringBounds(str, g.getFontRenderContext());
			g.drawString(str, (int) (p.x() + this.tileDiagX / zoom / 2 - bound.getWidth() / 2), (int) (p.y()  - bound.getHeight() / 2));
		}
		return p;
	}

	@Override
	public void renderInScreen(Graphics2D g, Renderable renderable, int tick) {
		renderable.renderInScreen(g, renderable.getX(), renderable.getY(), tick, zoom);
	}

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void setOrigin(int originX, int originY) {
		this.origX = originX;
		this.origY = originY;
	}
	/**
	 * Returns zoom.
	  * @return the value or object described above
	 */
	public int getZoom() {
		return zoom;
	}

	/**
	 * Sets zoom.
	  * @param zoom zoom factor
	 */
	public void setZoom(int zoom) {
		if (zoom <= 0) {
			throw new IllegalArgumentException("zoom must be positive");
		}
		this.zoom = zoom;
	}
	/**
	 * Returns tilediagx.
	  * @return the value or object described above
	 */
	public int getTileDiagX() { return tileDiagX; }
	/**
	 * Returns tilediagy.
	  * @return the value or object described above
	 */
	public int getTileDiagY() { return tileDiagY; }
}
