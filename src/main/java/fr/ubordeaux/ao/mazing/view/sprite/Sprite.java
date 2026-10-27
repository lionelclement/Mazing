package fr.ubordeaux.ao.mazing.view.sprite;

import fr.ubordeaux.ao.mazing.view.Orientation;

import java.awt.*;

public interface Sprite {
	/**
	 * Render the object on the screen.
	 *
	 * @param g              the graphics object to draw on.
	 * @param x              the x position to draw the object.
	 * @param y              the y position to draw the object.
	 * @param orientation    the orientation of the object.
	 * @param animationIndex the index of the animation
	 */
	void render(Graphics2D g, int x, int y, Orientation orientation, int animationIndex, int zoom);

	/// @return the size (in frames) of the animation
	int animationSize();

	/**
	 * Returns the horizontal anchor used when scaling this sprite.
	 * The offset is measured from the projected grid position in screen pixels.
	 * A value of zero preserves the default anchor for custom sprites.
	 *
	 * @param zoom the renderer zoom factor
	 * @return the horizontal anchor offset in screen pixels
	 */
	default double getScaleAnchorX(int zoom) {
		return 0.0;
	}

}
