package fr.ubordeaux.ao.mazing.view;

import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.*;

/**
 * Defines an object that can be positioned and rendered
 * in an isometric scene.
 */
public interface Renderable extends Comparable<Renderable> {
	/// Render the object on the screen.
	///
	/// @param g the graphics context used for drawing
	/// @param x    the x position to render the object
	/// @param y    the y position to render the object
	/// @param animationTick the current animation tick
	void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom);

	    /**
     * Returns the precise X coordinate in grid units.
     *
     * @return the X coordinate, including its fractional part
     */
    default double getPreciseX() {
        return getX();
    }

    /**
     * Returns the precise Y coordinate in grid units.
     *
     * @return the Y coordinate, including its fractional part
     */
    default double getPreciseY() {
        return getY();
    }

int getX();

	int getY();

	int getZ();

    /**
     * Returns the rendering layer of this object.
     * Objects in lower layers are rendered first.
     *
     * @return the rendering layer
     */
    default int getRenderLayer() {
        return 1;
    }

	default int getBlitOrder() {
		return 0;
	}

	@Override
	default int compareTo(Renderable o) {
        int layerComparison = Integer.compare(
                this.getRenderLayer(),
                o.getRenderLayer());

        if (layerComparison != 0) {
            return layerComparison;
        }


		int rX = o.getX() - this.getX();
		if (rX != 0) {
			return rX;
		}
		int rY = this.getY() - o.getY();
		if (rY != 0) {
			return rY;
		}
		int rZ = this.getZ() - o.getZ();
		if (rZ != 0) {
			return rZ;
		}
		return this.getBlitOrder() - o.getBlitOrder();
	}

	void setSprite(Sprite sprite);
}
