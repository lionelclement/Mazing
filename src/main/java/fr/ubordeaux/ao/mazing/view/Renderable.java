package fr.ubordeaux.ao.mazing.view;

import fr.ubordeaux.ao.mazing.view.sprite.Sprite;

import java.awt.*;

public interface Renderable extends Comparable<Renderable> {
	/// Render the object on the screen.
	///
	/// @param g    the graphics to render onto
	/// @param x    the x position to render the object
	/// @param y    the y position to render the object
	/// @param animationTick the amount of tick since the start of the render loop
	void renderInScreen(Graphics2D g, int x, int y, int animationTick, int zoom);

	int getX();

	int getY();

	int getZ();

	@Override
	default int compareTo(Renderable o) {
		int rX = o.getX() - this.getX();
		if (rX != 0) {
			return rX;
		}
		int rY = this.getY() - o.getY();
		if (rY != 0) {
			return rY;
		}
		return this.getZ() - o.getZ();
	}

	void setSprite(Sprite sprite);
}
