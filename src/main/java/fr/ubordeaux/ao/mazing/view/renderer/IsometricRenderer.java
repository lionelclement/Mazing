package fr.ubordeaux.ao.mazing.view.renderer;

import fr.ubordeaux.ao.mazing.view.Point;
import fr.ubordeaux.ao.mazing.view.Renderable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class IsometricRenderer implements Renderer {
	private int origX;
	private int origY;
	private final int tileDiagX;
	private final int tileDiagY;
	private final int halfTileDiagX;
	private final int halfTileDiagY;
	private final int tileHeight;
	private boolean debug;
	public int zoom;

	public IsometricRenderer(int origX, int origY, int tileDiagX, int tileDiagY, int tileHeight) {
		this.origX = origX;
		this.origY = origY;
		this.tileDiagX = tileDiagX;
		this.tileDiagY = tileDiagY;
		this.halfTileDiagX = tileDiagX / 2;
		this.halfTileDiagY = tileDiagY / 2;
		this.tileHeight = tileHeight;
		this.debug = false;
		this.zoom = 2;
	}

	@Override
	public Point renderInIsoGrid(Graphics2D g, Renderable renderable, int tick) {
		int screenX = this.origX + (halfTileDiagX/zoom) * (renderable.getX() + renderable.getY());
		int screenY = this.origY + (halfTileDiagY/zoom) * (renderable.getY() - renderable.getX() - 1) - renderable.getZ() * (this.tileHeight/zoom);
		renderable.renderInScreen(g, screenX, screenY, tick, zoom);
		if (debug) {
			var str = String.format("(%d, %d, %d)", renderable.getX(), renderable.getY(), renderable.getZ());
			Rectangle2D bound = g.getFont().getStringBounds(str, g.getFontRenderContext());
			g.drawString(str, (int) (screenX + this.tileDiagX / zoom / 2 - bound.getWidth() / 2), (int) (screenY  - bound.getHeight() / 2));
		}
		return new Point(screenX, screenY);
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

	@Override
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

}
