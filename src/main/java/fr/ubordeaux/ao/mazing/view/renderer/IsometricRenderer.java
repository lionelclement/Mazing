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
	private int zoom;

	public IsometricRenderer(int origX, int origY, int tileDiagX, int tileDiagY, int tileHeight) {
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

        double gridX = renderable.getPreciseX();
        double gridY = renderable.getPreciseY();

        double projectedX =
                origX + (halfTileDiagX / (double) zoom)
                * (gridX + gridY);

        double projectedY =
                origY + (halfTileDiagY / (double) zoom)
                * (gridY - gridX - 1)
                - renderable.getZ() * (tileHeight / (double) zoom);

        int screenX = (int) Math.round(projectedX);
        int screenY = (int) Math.round(projectedY);

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

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		if (zoom <= 0) {
			throw new IllegalArgumentException("zoom must be positive");
		}
		this.zoom = zoom;
	}
}
