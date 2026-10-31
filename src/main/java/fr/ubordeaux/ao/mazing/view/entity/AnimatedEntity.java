package fr.ubordeaux.ao.mazing.view.entity;

import fr.ubordeaux.ao.mazing.view.Orientation;
import fr.ubordeaux.ao.mazing.view.Renderable;
import fr.ubordeaux.ao.mazing.view.sprite.AnimatedOrientedSprite;
import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.Graphics2D;
import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * Represents an animated object in an isometric grid.
 * Its position, orientation, and current animation frame
 * determine how the object is rendered.
 */
public class AnimatedEntity implements Renderable {

	private AnimatedOrientedSprite sprite;
	private Orientation orientation;
	private double xPosition;
	private double yPosition;
	private double zPosition;
	private int blitOrder;
	private int currentFrameIndex;
    private boolean animationPaused;
    private IntConsumer animationFrameListener = frame -> {};
	private int lastAnimationTick;
    private double animationSpeed = 1.0;
    private double animationProgress = 0.0;
	private double scale = 1.0;
	private final ShadowMode shadowMode;

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	  * @param orientation orientation of the sprite
	  * @param xPosition horizontal grid position
	  * @param yPosition vertical grid position
	  * @param zPosition height in grid levels
	  * @param blitOrder rendering tie-break order
	 */
	public AnimatedEntity(
			AnimatedOrientedSprite sprite,
			Orientation orientation,
			int xPosition,
			int yPosition,
			int zPosition,
			int blitOrder) {

		this(sprite, orientation, xPosition, yPosition,
				zPosition, blitOrder, ShadowMode.WITHOUT_SHADOW);
	}

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	  * @param orientation orientation of the sprite
	  * @param xPosition horizontal grid position
	  * @param yPosition vertical grid position
	  * @param zPosition height in grid levels
	  * @param blitOrder rendering tie-break order
	  * @param shadowMode shadow rendering mode
	 */
	public AnimatedEntity(
			AnimatedOrientedSprite sprite,
			Orientation orientation,
			int xPosition,
			int yPosition,
			int zPosition,
			int blitOrder,
			ShadowMode shadowMode) {

		this.sprite = Objects.requireNonNull(sprite, "sprite must not be null");
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
		this.shadowMode = Objects.requireNonNull(shadowMode, "shadowMode must not be null");

		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.zPosition = zPosition;
		this.blitOrder = blitOrder;
		this.currentFrameIndex = 0;
		this.lastAnimationTick = -1;
	}

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	  * @param orientation orientation of the sprite
	  * @param xPosition horizontal grid position
	  * @param yPosition vertical grid position
	  * @param zPosition height in grid levels
	 */
	public AnimatedEntity(AnimatedOrientedSprite sprite, Orientation orientation, int xPosition, int yPosition,
			int zPosition) {
		this(sprite, orientation, xPosition, yPosition, zPosition, 0);
	}

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	 */
	public AnimatedEntity(AnimatedOrientedSprite sprite) {
		this(sprite, Orientation.NORTH, 0, 0, 0);
	}

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	  * @param xPosition horizontal grid position
	  * @param yPosition vertical grid position
	  * @param zPosition height in grid levels
	 */
	public AnimatedEntity(AnimatedOrientedSprite sprite, int xPosition, int yPosition, int zPosition) {
		this(sprite, Orientation.NORTH, xPosition, yPosition, zPosition);
	}

	/**
	 * Creates an instance of AnimatedEntity.
	  * @param sprite sprite to draw
	  * @param orientation orientation of the sprite
	 */
	public AnimatedEntity(AnimatedOrientedSprite sprite, Orientation orientation) {
		this(sprite, orientation, 0, 0, 0);
	}

	@Override
	public void updateAnimation(int animationTick) {
		if (animationPaused) {
            lastAnimationTick = animationTick;
        } else if (lastAnimationTick == -1) {
			lastAnimationTick = animationTick;
		} else {
			int elapsedTicks = animationTick - lastAnimationTick;
			if (elapsedTicks > 0) {
				animationProgress += elapsedTicks * animationSpeed;
				int framesToAdvance = (int) animationProgress;
				if (framesToAdvance > 0) {
				    for (int frame = 0; frame < framesToAdvance; frame++) {
				        if (animationPaused) {
				            break;
				        }
				        currentFrameIndex =
				                (currentFrameIndex + 1) % sprite.animationSize();
				        animationFrameListener.accept(currentFrameIndex);
				    }
				    animationProgress -= framesToAdvance;
				}
				lastAnimationTick = animationTick;
			}
		}
	}

	/**
	 * Resizes the animated character about its ground contact point.
	 * The same scale applies to all animation frames and their optional shadow.
	 *
	 * @param scale positive and finite visual size multiplier
	 */
	public void setScale(double scale) {
		if (!Double.isFinite(scale) || scale <= 0.0) {
			throw new IllegalArgumentException("scale must be positive and finite");
		}
		this.scale = scale;
	}

	@Override
	public java.awt.Rectangle getScreenBounds(int screenX, int screenY, int zoom) {
		java.awt.Rectangle bounds = sprite.getScreenBounds(screenX, screenY, zoom);
		if (bounds == null || scale == 1.0) {
			return bounds;
		}

		// Match the exact transform used for drawing, including the occlusion mask.
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

	@Override
	public void renderInScreen(Graphics2D graphics, int screenX, int screenY, int animationTick, int zoom) {
		updateAnimation(animationTick);
		if (scale == 1.0) {
			sprite.render(graphics, screenX, screenY, orientation,
					currentFrameIndex, zoom, shadowMode == ShadowMode.WITH_SHADOW);
			return;
		}

		// Keep the projected ground contact fixed when making a character smaller.
		double anchorX = screenX + sprite.getScaleAnchorX(zoom);
		Graphics2D scaled = (Graphics2D) graphics.create();
		try {
			scaled.translate(anchorX, screenY);
			scaled.scale(scale, scale);
			scaled.translate(-anchorX, -screenY);
			sprite.render(scaled, screenX, screenY, orientation,
					currentFrameIndex, zoom, shadowMode == ShadowMode.WITH_SHADOW);
		} finally {
			scaled.dispose();
		}
	}

	    /**
     * Sets the animation playback speed.
     *
     * A value of 1.0 uses the normal animation speed.
     * A value of 2.0 plays the animation twice as fast.
     * A value of 0.5 plays the animation at half speed.
     *
     * @param animationSpeed a positive playback speed multiplier
     */
    public void setAnimationSpeed(double animationSpeed) {
        if (!Double.isFinite(animationSpeed) || animationSpeed <= 0) {
            throw new IllegalArgumentException(
                    "Animation speed must be positive and finite");
        }
        this.animationSpeed = animationSpeed;
    }

    /**
     * Returns the animation playback speed multiplier.
     *
     * @return the animation speed multiplier
     */
    public double getAnimationSpeed() {
        return animationSpeed;
    }


    /**
     * Registers a callback invoked whenever a new animation
     * frame is reached.
     *
     * @param listener callback receiving the new frame index
     */
    public void setAnimationFrameListener(IntConsumer listener) {
        this.animationFrameListener =
                Objects.requireNonNull(listener, "listener must not be null");
    }


    /**
     * Pauses or resumes the current animation.
     *
     * The current frame remains visible while paused.
     *
     * @param paused true to pause the animation
     */
    public void setAnimationPaused(boolean paused) {
        this.animationPaused = paused;
    }

    /**
     * Returns whether the animation is paused.
     *
     * @return true if the animation is paused
     */
    public boolean isAnimationPaused() {
        return animationPaused;
    }

/**
 * Returns sprite.
  * @return the value or object described above
 */
public Sprite getSprite() {
		return sprite;
	}

	/**
	 * Returns orientation.
	  * @return the value or object described above
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Sets orientation.
	  * @param orientation orientation of the sprite
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = Objects.requireNonNull(orientation, "orientation must not be null");
	}

	@Override
    public double getPreciseX() {
        return xPosition;
    }

    @Override
    public double getPreciseY() {
        return yPosition;
    }

    /**
     * Places the animated entity at an exact position in world coordinates.
     * Positive X points east, positive Y points north, and positive Z is up.
     * Changing position does not change orientation or restart the animation.
     *
     * @param x east-west position, in tile units
     * @param y north-south position, in tile units
     * @param z elevation, in world levels
     * @throws IllegalArgumentException if a coordinate is not finite
     */
    public void setPosition(double x, double y, double z) {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
            throw new IllegalArgumentException("Position coordinates must be finite");
        }
        xPosition = x;
        yPosition = y;
        zPosition = z;
    }

    /**
     * Moves the entity by a fractional distance in grid units.
     *
     * @param deltaX displacement along the X axis
     * @param deltaY displacement along the Y axis
     */
    public void moveBy(double deltaX, double deltaY) {
        xPosition += deltaX;
        yPosition += deltaY;
    }

    /**
     * Moves the entity and updates its facing direction.
     *
     * @param deltaX displacement along the X axis
     * @param deltaY displacement along the Y axis
     */
    public void moveInDirection(double deltaX, double deltaY) {
        if (deltaX > 0 && deltaY == 0) {
            setOrientation(Orientation.EAST);
        } else if (deltaX < 0 && deltaY == 0) {
            setOrientation(Orientation.WEST);
        } else if (deltaY > 0 && deltaX == 0) {
            setOrientation(Orientation.NORTH);
        } else if (deltaY < 0 && deltaX == 0) {
            setOrientation(Orientation.SOUTH);
        }

        moveBy(deltaX, deltaY);
    }

public int getX() {
		return (int) Math.round(xPosition);
	}

	/**
	 * Sets x.
	  * @param xPosition horizontal grid position
	 */
	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getY() {
		return (int) Math.round(yPosition);
	}

	/**
	 * Sets y.
	  * @param yPosition vertical grid position
	 */
	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getZ() {
		return (int) Math.round(zPosition);
	}

	/**
	 * Paints a climbing character after the raised floor from the first step.
	 * Its true Z coordinate remains continuous for projection and movement.
	 */
	@Override
	public int getRenderLevel() {
		return (int) Math.ceil(zPosition);
	}

	@Override
	public double getPreciseZ() {
		return zPosition;
	}

	/**
	 * Sets the height, including fractions of a level.
	 * @param zPosition height in grid levels
	 */
	public void setPreciseZ(double zPosition) {
		if (!Double.isFinite(zPosition)) {
			throw new IllegalArgumentException("Height must be finite");
		}
		this.zPosition = zPosition;
	}

	@Override
	public int getBlitOrder() {
		return blitOrder;
	}

	/**
	 * Sets z.
	  * @param zPosition height in grid levels
	 */
	public void setZ(int zPosition) {
		this.zPosition = zPosition;
	}

	/**
	 * Moves the object along the selected grid axes.
	  * @param deltaX horizontal displacement
	 */
	public void moveX(int deltaX) {
		this.xPosition += deltaX;
	}

	/**
	 * Moves the object along the selected grid axes.
	  * @param deltaY vertical displacement
	 */
	public void moveY(int deltaY) {
		this.yPosition += deltaY;
	}

	/**
	 * Moves the object along the selected grid axes.
	  * @param deltaZ elevation displacement
	 */
	public void moveZ(int deltaZ) {
		this.zPosition += deltaZ;
	}

	/**
	 * Moves the object along the selected grid axes.
	  * @param deltaX horizontal displacement
	  * @param deltaY vertical displacement
	 */
	public void move(int deltaX, int deltaY) {
		this.xPosition += deltaX;
		this.yPosition += deltaY;
	}

	/**
	 * Moves the object along the selected grid axes.
	  * @param deltaX horizontal displacement
	  * @param deltaY vertical displacement
	  * @param deltaZ elevation displacement
	 */
	public void move(int deltaX, int deltaY, int deltaZ) {
		this.xPosition += deltaX;
		this.yPosition += deltaY;
		this.zPosition += deltaZ;
	}

	@Override
	public void setSprite(Sprite sprite) {
		if (!(sprite instanceof AnimatedOrientedSprite animatedSprite)) {
			throw new IllegalArgumentException("AnimatedEntity requires an AnimatedOrientedSprite");
		}
		this.sprite = animatedSprite;
		this.currentFrameIndex = 0;
		this.animationProgress = 0.0;
		this.lastAnimationTick = -1;
	}
}
