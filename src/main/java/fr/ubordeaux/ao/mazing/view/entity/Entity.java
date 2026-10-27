package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.Graphics2D;
import java.util.Objects;

/**
 * Represents an object positioned in a three-dimensional
 * isometric grid.
 */
public class Entity implements Renderable {

	private Sprite sprite;
	private Orientation orientation;
	private int xPosition;
	private int yPosition;
	private int zPosition;
	private double scale = 1.0;

	public Entity(Sprite sprite, Orientation orientation, int xPosition, int yPosition, int zPosition) {
		this.sprite = Objects.requireNonNull(sprite, "sprite must not be null");
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.zPosition = zPosition;
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

	public Sprite getSprite() {
		return sprite;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
	}

	public int getX() {
		return xPosition;
	}

	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getY() {
		return yPosition;
	}

	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getZ() {
		return zPosition;
	}

	public void setZ(int zPosition) {
		this.zPosition = zPosition;
	}

	public void moveX(int deltaX) {
		this.xPosition += deltaX;
	}

	public void moveY(int deltaY) {
		this.yPosition += deltaY;
	}

	public void moveZ(int deltaZ) {
		this.zPosition += deltaZ;
	}

	public void move(int deltaX, int deltaY) {
		this.xPosition += deltaX;
		this.yPosition += deltaY;
	}

	public void move(int deltaX, int deltaY, int deltaZ) {
		this.xPosition += deltaX;
		this.yPosition += deltaY;
		this.zPosition += deltaZ;
	}

	@Override
	public void setSprite(Sprite sprite) {
		this.sprite = Objects.requireNonNull(sprite, "sprite must not be null");
	}
}
