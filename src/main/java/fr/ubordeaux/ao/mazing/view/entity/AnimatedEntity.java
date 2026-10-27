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
	private int zPosition;
	private int blitOrder;
	private int currentFrameIndex;
    private IntConsumer animationFrameListener = frame -> {};
	private int lastAnimationTick;
    private double animationSpeed = 1.0;
    private double animationProgress = 0.0;
	private final ShadowMode shadowMode;

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

	public AnimatedEntity(AnimatedOrientedSprite sprite, Orientation orientation, int xPosition, int yPosition,
			int zPosition) {
		this(sprite, orientation, xPosition, yPosition, zPosition, 0);
	}

	public AnimatedEntity(AnimatedOrientedSprite sprite) {
		this(sprite, Orientation.NORTH, 0, 0, 0);
	}

	public AnimatedEntity(AnimatedOrientedSprite sprite, int xPosition, int yPosition, int zPosition) {
		this(sprite, Orientation.NORTH, xPosition, yPosition, zPosition);
	}

	public AnimatedEntity(AnimatedOrientedSprite sprite, Orientation orientation) {
		this(sprite, orientation, 0, 0, 0);
	}

	@Override
	public void renderInScreen(Graphics2D graphics, int screenX, int screenY, int animationTick, int zoom) {
		// update our animation counter and sprite to display, according to the new
		// animation tick
		if (lastAnimationTick == -1) {
			lastAnimationTick = animationTick;
		} else {
			int elapsedTicks = animationTick - lastAnimationTick;
			if (elapsedTicks > 0) {
				animationProgress += elapsedTicks * animationSpeed;
				int framesToAdvance = (int) animationProgress;
				if (framesToAdvance > 0) {
				    for (int frame = 0; frame < framesToAdvance; frame++) {
				        currentFrameIndex =
				                (currentFrameIndex + 1) % sprite.animationSize();
				        animationFrameListener.accept(currentFrameIndex);
				    }
				    animationProgress -= framesToAdvance;
				}
				lastAnimationTick = animationTick;
			}
		}
		sprite.render(
				graphics,
				screenX,
				screenY,
				orientation,
				currentFrameIndex,
				zoom,
				shadowMode == ShadowMode.WITH_SHADOW);
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

public Sprite getSprite() {
		return sprite;
	}

	public Orientation getOrientation() {
		return orientation;
	}

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
            setOrientation(Orientation.SOUTH);
        } else if (deltaY < 0 && deltaX == 0) {
            setOrientation(Orientation.NORTH);
        }

        moveBy(deltaX, deltaY);
    }

public int getX() {
		return (int) Math.round(xPosition);
	}

	public void setX(int xPosition) {
		this.xPosition = xPosition;
	}

	public int getY() {
		return (int) Math.round(yPosition);
	}

	public void setY(int yPosition) {
		this.yPosition = yPosition;
	}

	public int getZ() {
		return zPosition;
	}

	@Override
	public int getBlitOrder() {
		return blitOrder;
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
		if (!(sprite instanceof AnimatedOrientedSprite animatedSprite)) {
			throw new IllegalArgumentException("AnimatedEntity requires an AnimatedOrientedSprite");
		}
		this.sprite = animatedSprite;
		this.currentFrameIndex = 0;
		this.animationProgress = 0.0;
		this.lastAnimationTick = -1;
	}
}
