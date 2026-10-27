package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

/// A Debug renderer. Not to be used outside the API.
/// Made public due to technical limitation.
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
		int dy = halfTileDiagY/zoom * (renderable.getY() - renderable.getX() - 1) - renderable.getZ() * (this.tileHeight/zoom);
		int y = dy / (this.tileDiagY/zoom);
		int x = dx / (this.tileDiagX/zoom);
		// if (dy <= 0) {
		// 	// g.drawRect(screenX, screenY, 30, 30);
		// 	// g.drawString(String.format("(%d, %d, %d)", dy, y,0), screenX, screenY);
		// 	// dy -= this.tileDiagY;
		// }
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
	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		if (zoom <= 0) {
			throw new IllegalArgumentException("zoom must be positive");
		}
		this.zoom = zoom;
	}
	public int getTileDiagX() { return tileDiagX; }
	public int getTileDiagY() { return tileDiagY; }
}
