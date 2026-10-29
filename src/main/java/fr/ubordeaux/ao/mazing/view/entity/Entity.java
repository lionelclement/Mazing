package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowCaster;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometry;
import fr.ubordeaux.ao.mazing.view.shadow.ShadowGeometryProvider;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.Graphics2D;
import java.util.Objects;

/**
 * Represents an object positioned in a three-dimensional
 * isometric grid.
 */
public class Entity implements Renderable, ShadowCaster {

	private Sprite sprite;
	private Orientation orientation;
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private double preciseXPosition;
	private double preciseYPosition;
	private double preciseZPosition;
	private double scale = 1.0;
	private int renderOffsetX;
	private int renderOffsetY;
	private ShadowGeometry shadowGeometry;

	/** Creates a tile-aligned entity (legacy integer coordinates). */
	public Entity(Sprite sprite, Orientation orientation,
			int xPosition, int yPosition, int zPosition) {
		this(sprite, orientation, (double) xPosition,
				(double) yPosition, (double) zPosition);
	}

	/**
	 * Creates a decor entity at an exact world-space position. The integer
	 * getters remain available for legacy grid operations; rendering, depth
	 * ordering, occlusion and shadows use the precise coordinates.
	 */
	public Entity(Sprite sprite, Orientation orientation,
			double xPosition, double yPosition, double zPosition) {
		this.sprite = Objects.requireNonNull(sprite, "sprite must not be null");
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
		setPrecisePosition(xPosition, yPosition);
		setPreciseZ(zPosition);
	}

	/**
	 * Creates an entity with a custom visual scale.
	 * The scale changes only the image size, not the grid position.
	 *
	 * @param sprite the sprite to render
	 * @param orientation the sprite orientation
	 * @param xPosition the X coordinate on the grid
	 * @param yPosition the Y coordinate on the grid
	 * @param zPosition the Z coordinate on the grid
	 * @param scale visual size multiplier (1.0 is the original size)
	 */
	public Entity(Sprite sprite, Orientation orientation,
			int xPosition, int yPosition, int zPosition, double scale) {
		this(sprite, orientation, xPosition, yPosition, zPosition);
		setScale(scale);
	}

	/** Exact position and custom visual scale (independent parameters). */
	public Entity(Sprite sprite, Orientation orientation,
			double xPosition, double yPosition, double zPosition, double scale) {
		this(sprite, orientation, xPosition, yPosition, zPosition);
		setScale(scale);
	}

	public Entity(Sprite sprite) {
		this(sprite, Orientation.NORTH, 0, 0, 0);
	}

	public Entity(Sprite sprite, int xPosition, int yPosition, int zPosition) {
		this(sprite, Orientation.NORTH, xPosition, yPosition, zPosition);
	}

	public Entity(Sprite sprite, Orientation orientation) {
		this(sprite, orientation, 0, 0, 0);
	}


	@Override
	public void renderInScreen(Graphics2D graphics, int screenX, int screenY, int animationTick, int zoom) {
		screenX += renderOffsetX / zoom;
		screenY += renderOffsetY / zoom;
		if (scale == 1.0) {
			sprite.render(graphics, screenX, screenY, orientation, animationTick, zoom);
			return;
		}

		// The projected grid point is at the left edge of an atlas cell,
		// not at the object's horizontal center. Scaling about screenX
		// would therefore make enlarged objects drift to the right.
		// Keep the horizontal atlas center and the ground reference fixed.
		double anchorX = screenX + sprite.getScaleAnchorX(zoom);
		Graphics2D scaledGraphics = (Graphics2D) graphics.create();
		try {
			scaledGraphics.translate(anchorX, screenY);
			scaledGraphics.scale(scale, scale);
			scaledGraphics.translate(-anchorX, -screenY);
			sprite.render(scaledGraphics, screenX, screenY, orientation, animationTick, zoom);
		} finally {
			scaledGraphics.dispose();
		}
	}

	/** Moves the sprite relative to its grid anchor, in source-image pixels. */
	public void setRenderOffset(int xPixels, int yPixels) {
		this.renderOffsetX = xPixels;
		this.renderOffsetY = yPixels;
	}

	/** Horizontal image offset, in original sprite pixels. */
	public int getRenderOffsetX() {
		return renderOffsetX;
	}

	/** Vertical image offset, in original sprite pixels. */
	public int getRenderOffsetY() {
		return renderOffsetY;
	}

	@Override
	public java.awt.Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
		screenX += renderOffsetX / zoom;
		screenY += renderOffsetY / zoom;
		java.awt.Rectangle bounds = sprite.getScreenBounds(screenX, screenY, zoom);
		if (bounds == null || scale == 1.0) {
			return bounds;
		}

		// Apply the same anchor-based transform as renderInScreen().
		double anchorX = screenX + sprite.getScaleAnchorX(zoom);
		double left = anchorX + scale * (bounds.getMinX() - anchorX);
		double right = anchorX + scale * (bounds.getMaxX() - anchorX);
		double top = screenY + scale * (bounds.getMinY() - screenY);
		double bottom = screenY + scale * (bounds.getMaxY() - screenY);
		int x = (int) Math.floor(left) - 1;
		int y = (int) Math.floor(top) - 1;
		return new java.awt.Rectangle(x, y,
				(int) Math.ceil(right) - x + 1,
				(int) Math.ceil(bottom) - y + 1);
	}

	/**
	 * Returns the visual size multiplier of this entity.
	 *
	 * @return the visual scale (1.0 is the original size)
	 */
	public double getScale() {
		return scale;
	}

	/**
	 * Changes the visual size of this entity without moving it on the grid.
	 *
	 * @param scale a positive, finite size multiplier
	 * @throws IllegalArgumentException if the scale is not positive or finite
	 */
	public void setScale(double scale) {
		if (!Double.isFinite(scale) || scale <= 0.0) {
			throw new IllegalArgumentException("scale must be positive and finite");
		}
		this.scale = scale;
	}

	/**
	 * Overrides the generic physical approximation used for this static object.
	 * Changes to scene geometry require ViewPanel.invalidateStaticShadows().
	 */
	public void setShadowGeometry(ShadowGeometry geometry) {
		this.shadowGeometry = Objects.requireNonNull(geometry, "geometry");
	}

	@Override
	public ShadowGeometry shadowGeometry() {
		return shadowGeometry != null ? shadowGeometry : defaultShadowGeometry();
	}

	/**
	 * Explicit sprite geometry takes precedence. Ordinary furniture has no
	 * fixed box: the shadow renderer estimates its dimensions from the
	 * actual rendered PNG silhouette and the grid's projection.
	 */
	protected ShadowGeometry defaultShadowGeometry() {
		if (sprite instanceof ShadowGeometryProvider provider) {
			return provider.shadowGeometry(orientation);
		}
		return null;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
	}

	/** Rounded X coordinate for clients using an integer grid. */
	public int getX() {
		return xPosition;
	}

	/** Exact X coordinate used by the renderer and occlusion logic. */
	@Override
	public double getPreciseX() {
		return preciseXPosition;
	}

	/** Sets integer X, deliberately snapping only that axis to the grid. */
	public void setX(int xPosition) {
		setPrecisePosition(xPosition, preciseYPosition);
	}

	/** Rounded Y coordinate for clients using an integer grid. */
	public int getY() {
		return yPosition;
	}

	/** Exact Y coordinate used by the renderer and occlusion logic. */
	@Override
	public double getPreciseY() {
		return preciseYPosition;
	}

	/** Sets integer Y, deliberately snapping only that axis to the grid. */
	public void setY(int yPosition) {
		setPrecisePosition(preciseXPosition, yPosition);
	}

	/**
	 * Moves an object to a precise horizontal position. For a static scene,
	 * call ViewPanel.invalidateStaticShadows() after changing a shadow caster.
	 */
	public void setPrecisePosition(double x, double y) {
		// Validate both coordinates before changing any state.
		int roundedX = roundedCoordinate(x);
		int roundedY = roundedCoordinate(y);
		this.preciseXPosition = x;
		this.preciseYPosition = y;
		this.xPosition = roundedX;
		this.yPosition = roundedY;
	}

	private static int roundedCoordinate(double value) {
		if (!Double.isFinite(value) || value < Integer.MIN_VALUE
				|| value > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"coordinate must be finite and in int range");
		}
		return (int) Math.round(value);
	}

	/** Moves an entity by an exact horizontal displacement. */
	public void moveBy(double dx, double dy) {
		setPrecisePosition(preciseXPosition + dx, preciseYPosition + dy);
	}

	public int getZ() {
		return zPosition;
	}

	/** Exact elevation, including fractional platform heights, in world levels. */
	@Override
	public double getPreciseZ() {
		return preciseZPosition;
	}

	/** Set elevation independently of horizontal coordinates. */
	public void setPreciseZ(double zPosition) {
		if (!Double.isFinite(zPosition)
				|| zPosition < Integer.MIN_VALUE
				|| zPosition > Integer.MAX_VALUE) {
			throw new IllegalArgumentException("Elevation must be finite and in int range");
		}
		this.preciseZPosition = zPosition;
		// Legacy rendering level for fractional floors/objects.
		this.zPosition = (int) Math.ceil(zPosition);
	}

	public void setZ(int zPosition) {
		setPreciseZ(zPosition);
	}

	public void moveX(int deltaX) {
		moveBy(deltaX, 0);
	}

	public void moveY(int deltaY) {
		moveBy(0, deltaY);
	}

	public void moveZ(int deltaZ) {
		setPreciseZ(preciseZPosition + deltaZ);
	}

	public void move(int deltaX, int deltaY) {
		moveBy(deltaX, deltaY);
	}

	public void move(int deltaX, int deltaY, int deltaZ) {
		moveBy(deltaX, deltaY);
		moveZ(deltaZ);
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = Objects.requireNonNull(sprite, "sprite must not be null");
	}
}
